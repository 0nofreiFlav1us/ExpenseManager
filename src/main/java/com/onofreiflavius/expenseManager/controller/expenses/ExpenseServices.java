package com.onofreiflavius.expenseManager.controller.expenses;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.onofreiflavius.expenseManager.controller.users.UserServices;
import com.onofreiflavius.expenseManager.model.ExpenseModel;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ExpenseServices {

    @Value("${api.key}")
    private String apiKey;
    private static final Logger log = LoggerFactory.getLogger(ExpenseServices.class);
    private final ExpenseRepository expenseRepository;
    private final UserServices userServices;
    private String username;

    public ExpenseServices(ExpenseRepository expensesRepository, UserServices userServices) {
        this.expenseRepository = expensesRepository;
        this.userServices = userServices;
        username = null;
    }


    //               ||
    //    Methods    ||
    //               \/

    // This sets the username
    public void setUsername(String username) {
        this.username = username;
    }

    // This fetches all the data
    public List<ExpenseModel> getExpenses(String username) {
        return expenseRepository.getExpenses(username);
    }

    public String dateToString(Integer day, Integer month) {
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        return day + " " + months[month-1];
    }

    // This adds new expenses
    public void add(List<ExpenseModel> newExpenses) {

        String dateTmp = newExpenses.getFirst().getDate().replace("*", "");
        String date = "*" + dateTmp;                                  // I mark the first purchase of the day because it
        if(expenseRepository.dateExistence(date, -1) == 0)         // helps me divide the days, and then I can easily create
            newExpenses.getFirst().setDate(date);                     // table rows for the total daily expenditures

        expenseRepository.saveAll(newExpenses);
    }

    // This searches for a specific expense by id
    public Optional<ExpenseModel> findById(Integer id) {
        return expenseRepository.findById(id);
    }

    // This updates an existing expense
    public void update(Integer id, ExpenseModel updatedExpense) {
        Optional<ExpenseModel> existingExpense = findById(id);
        if(existingExpense.isPresent()) {
            existingExpense.get().setProduct(updatedExpense.getProduct());
            existingExpense.get().setPrice(updatedExpense.getPrice());
            existingExpense.get().setQuantity(updatedExpense.getQuantity());
            existingExpense.get().setStore(updatedExpense.getStore());
            existingExpense.get().setDate(updatedExpense.getDate());
            existingExpense.get().setDay(updatedExpense.getDay());
            existingExpense.get().setMonth(updatedExpense.getMonth());
            existingExpense.get().setWasteful(updatedExpense.getWasteful());

            String dateTmp = updatedExpense.getDate().replace("*", "");
            String date = "*" + dateTmp;
            if(expenseRepository.dateExistence(date, id) == 0)
                existingExpense.get().setDate(date);

            expenseRepository.save(existingExpense.get());
        }
    }

    // This deletes an expense
    public void delete(Integer id) {
        Optional<ExpenseModel> existingExpense = findById(id);
        if(existingExpense.isPresent() && existingExpense.get().getDate().charAt(0) == '*') {
            String date = existingExpense.get().getDate().replace("*", "");

            Optional<ExpenseModel> lastExpense = expenseRepository.lastExpense(date);
            if(lastExpense.isPresent()) {
                lastExpense.get().setDate(existingExpense.get().getDate());
                expenseRepository.save(lastExpense.get());
            }
        }

        expenseRepository.deleteById(id);
    }


    //                    ||
    //  Cloud Vision API  ||
    //      Methods       \/

    // This builds the HTTP Request, then sends it to Google's API
    public void readReceipt(String base64String, String store, String day, String month, String username) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(new URI("https://vision.googleapis.com/v1/images:annotate?key=" + apiKey))
                .POST(HttpRequest.BodyPublishers.ofString("{\n" +
                        "  \"requests\": [\n" +
                        "    {\n" +
                        "      \"image\": {\n" +
                        "        \"content\": \"" + base64String +"\"\n" +
                        "      },\n" +
                        "      \"features\": [\n" +
                        "        {\n" +
                        "          \"type\": \"TEXT_DETECTION\"\n" +
                        "        }\n" +
                        "      ]\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}\n"))
                .build();

        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpResponse<String> postResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());

            extractExpenses(postResponse.body(), store, day, month, username);
        }
        catch (UncheckedIOException e) {
            log.error("HTTP ERROR: ", e);
        }
    }

    // This extracts the "description" field from the API Response because that's where the text of the receipt is
    private void extractExpenses(String receipt, String store, String day, String month, String username) {
        JSONObject receiptObject= new JSONObject(receipt);
        JSONArray responsesArray = receiptObject.getJSONArray("responses");
        JSONObject responsesObject = responsesArray.getJSONObject(0);
        JSONArray textAnnotations = responsesObject.getJSONArray("textAnnotations");
        JSONObject textAnnotationsObject = textAnnotations.getJSONObject(0);

        String expensesData = textAnnotationsObject.getString("description");

        prepareExpenses(expensesData, store, day, month, username);
    }

    // This processes the text from the receipt, then creates a List of objects
    private void prepareExpenses(String expensesData, String store, String day, String month, String username) {
        // Product, Price, Quantity, Store (parameter), Date
        String product;
        double price;
        double quantity;

        // The list of expenses
        List<ExpenseModel> newExpenses = new ArrayList<>();

        // Helper variables
        String[] lines = expensesData.split("\n");
        String[] parts;
        int index = 0;
        int nrOfLines = lines.length;

        // Extracting the necessary data
        while (index + 2 <= nrOfLines) {
            if ((lines[index].toLowerCase().contains("x") && lines[index].toLowerCase().charAt(0) != 'k') || lines[index].matches("\\d[.,]\\d\\d\\d")) {
                try {
                    parts = lines[index].split(" ");
                    String quantityTmp = parts[0].replace(",", ".").replace(":", ".");
                    quantity = Double.parseDouble(quantityTmp);
                    index++; // QUANTITY EXTRACTED

                    String leiCase = lines[index].toLowerCase();
                    if (leiCase.equals("l") || leiCase.equals("le") || leiCase.equals("lei"))
                        index++;

                    if(lines[index].toLowerCase().matches("kg\\sx\\s\\d[,.]\\d\\d"))
                        index++;

                    if (lines[index].matches("\\d[.,]\\d\\d(\\s)?[aAbB]")) {
                        parts = lines[index].split(" ");
                        String priceTmp = parts[0].replace(",", ".");
                        price = Double.parseDouble(priceTmp);
                        index++; // PRICE EXTRACTED

                        product = lines[index].substring(0, 1).toUpperCase() + lines[index].substring(1).toLowerCase();
                        index++; // PRODUCT EXTRACTED
                    } else {
                        product = lines[index].substring(0, 1).toUpperCase() + lines[index].substring(1).toLowerCase();
                        index++; // PRODUCT EXTRACTED

                        parts = lines[index].split(" ");
                        String priceTmp = parts[0].replace(",", ".");
                        price = Double.parseDouble(priceTmp);
                        index++; // PRICE EXTRACTED
                    }

                    // Preparing the list of expenses
                    Integer dayI = Integer.parseInt(day);
                    Integer monthI = Integer.parseInt(month);
                    String date = dateToString(dayI, monthI);

                    ExpenseModel expense = new ExpenseModel(product, price, quantity, store, date, dayI, monthI, username);
                    newExpenses.add(expense);
                } catch (Exception e) {
                    log.error("ERROR: ", new RuntimeException(e));
                    index++;
                }
            } else
                index++;
        }
        if( ! newExpenses.isEmpty()) // The List of objects is sent to the local method "add()",
            add(newExpenses);       // where the expenses will be saved in the database
    }

}