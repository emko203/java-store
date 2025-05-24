package com.example.store;

import com.example.store.model.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        // Create a new scanner instance for this run
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        // Create a store with specific markup and discount settings
        Store store = new Store("Java Store", 20.0, 30.0, 7, 15.0);
        
        // Add products
        FoodProduct milk = new FoodProduct("F001", "Мляко", 2.50, 
                                         LocalDate.now().plusDays(5), 100);
        FoodProduct bread = new FoodProduct("F002", "Хляб", 1.20, 
                                          LocalDate.now().plusDays(3), 50);
        NonFoodProduct soap = new NonFoodProduct("NF001", "Сапун", 3.00, 
                                               LocalDate.now().plusMonths(6), 200);
        
        store.addProduct(milk);
        store.addProduct(bread);
        store.addProduct(soap);

        // Add cashiers and registers
        Cashier cashier1 = new Cashier("C001", "Иван Иванов", 1500.0);
        Cashier cashier2 = new Cashier("C002", "Мария Петрова", 1600.0);
        CashRegister register1 = new CashRegister("R001");
        CashRegister register2 = new CashRegister("R002");
        
        store.addCashier(cashier1);
        store.addCashier(cashier2);
        store.addCashRegister(register1);
        store.addCashRegister(register2);
        
        cashier1.assignToRegister(register1);
        cashier2.assignToRegister(register2);

        while (running) {
            System.out.println("\n=== Меню на Java Store ===");
            System.out.println("1. Покажи налични продукти");
            System.out.println("2. Направи покупка");
            System.out.println("3. Покажи статистика на магазина");
            System.out.println("4. Покажи изтекли продукти");
            System.out.println("5. Покажи продукти близо до изтичане");
            System.out.println("6. Изход");
            System.out.print("Изберете опция: ");

            String input = scanner.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Невалиден избор. Моля, въведете число.");
                continue;
            }

            switch (choice) {
                case 1:
                    store.displayAvailableProducts();
                    break;
                case 2:
                    processPurchase(store, register1, scanner);
                    break;
                case 3:
                    displayStoreStatistics(store);
                    break;
                case 4:
                    displayExpiredProducts(store);
                    break;
                case 5:
                    displayNearExpirationProducts(store);
                    break;
                case 6:
                    running = false;
                    System.out.println("Благодарим ви, че използвахте Java Store!");
                    break;
                default:
                    System.out.println("Невалиден избор. Моля, опитайте отново.");
            }
        }
        
        // Close scanner at the end of main
        scanner.close();
    }

    private static void processPurchase(Store store, CashRegister register, Scanner scanner) {
        Map<Product, Integer> saleItems = new HashMap<>();
        List<Product> availableProducts = store.getProducts().stream()
            .filter(p -> p.getQuantity() > 0)
            .collect(Collectors.toList());

        if (availableProducts.isEmpty()) {
            System.out.println("Няма налични продукти за продажба.");
            return;
        }

        System.out.println("\nНалични продукти:");
        for (Product product : availableProducts) {
            System.out.printf("%s - %s (%.2f лв., наличност: %d)\n",
                product.getId(), product.getName(), store.calculateProductPrice(product), product.getQuantity());
        }

        while (true) {
            System.out.print("\nВъведете ID на продукт (или 'край' за завършване): ");
            String productId = scanner.nextLine().trim();

            if (productId.equalsIgnoreCase("край")) {
                break;
            }

            Optional<Product> productOpt = availableProducts.stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst();

            if (productOpt.isEmpty()) {
                System.out.println("Невалиден ID на продукт. Моля, опитайте отново.");
                continue;
            }

            Product product = productOpt.get();
            System.out.print("Въведете количество: ");
            String quantityInput = scanner.nextLine().trim();
            int quantity;
            try {
                quantity = Integer.parseInt(quantityInput);
                if (quantity <= 0) {
                    System.out.println("Количеството трябва да е по-голямо от 0.");
                    continue;
                }
                if (quantity > product.getQuantity()) {
                    System.out.printf("Няма достатъчно наличност. Максимално количество: %d\n", 
                        product.getQuantity());
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println("Моля, въведете валидно число.");
                continue;
            }

            saleItems.put(product, quantity);
        }

        if (!saleItems.isEmpty()) {
            try {
                store.processSale(register, saleItems);
                System.out.println("\nПокупката е завършена успешно!");
                System.out.println("Касовият бон е запазен във файл.");
            } catch (Exception e) {
                System.err.println("Грешка при обработка на покупката: " + e.getMessage());
            }
        }
    }

    public static void displayStoreStatistics(Store store) {
        System.out.println("\nСтатистика на магазина:");
        System.out.println("------------------");
        System.out.printf("Общ приход: %.2f лв.%n", store.getTotalRevenue());
        System.out.printf("Общи разходи за доставка: %.2f лв.%n", store.getTotalDeliveryCosts());
        System.out.printf("Общи разходи за заплати: %.2f лв.%n", store.getTotalSalaryCosts());
        System.out.printf("Обща печалба: %.2f лв.%n", store.calculateProfit());
        System.out.printf("Общ брой касови бележки: %d%n", store.getTotalReceiptsCount());
        System.out.println("------------------");
    }

    public static void displayExpiredProducts(Store store) {
        List<Product> expiredProducts = store.getExpiredProducts();
        System.out.println("\nИзтекли продукти:");
        System.out.println("------------------");
        if (expiredProducts.isEmpty()) {
            System.out.println("Няма изтекли продукти.");
        } else {
            expiredProducts.forEach(p -> 
                System.out.printf("- %s (ID: %s, Дата на изтичане: %s)%n", 
                    p.getName(), p.getId(), p.getExpirationDate()));
        }
        System.out.println("------------------");
    }

    public static void displayNearExpirationProducts(Store store) {
        List<Product> nearExpProducts = store.getProductsNearExpiration();
        System.out.println("\nПродукти близо до изтичане:");
        System.out.println("------------------");
        if (nearExpProducts.isEmpty()) {
            System.out.println("Няма продукти близо до изтичане.");
        } else {
            nearExpProducts.forEach(p -> 
                System.out.printf("- %s (ID: %s, Дата на изтичане: %s, Количество: %d)%n", 
                    p.getName(), p.getId(), p.getExpirationDate(), p.getQuantity()));
        }
        System.out.println("------------------");
    }
} 