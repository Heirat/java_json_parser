import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Parser {
    // Таблица для хранения полученных из Json записей об автомобилях
    private final ArrayList<ArrayList<String>> table;
    private final JsonParser jsonParser;

    public ArrayList<ArrayList<String>> getTable() {
        return table;
    }

    public Parser(String filePath) throws IOException {
        table = new ArrayList<>();
        FileReader fileReader = new FileReader(filePath);
        BufferedReader reader = new BufferedReader(fileReader);
        JsonFactory jsonFactory = new JsonFactory();
        jsonParser = jsonFactory.createParser(reader);

        this.readJson();
    }

    /**
     * Обходит объекты брендов в первичном массиве и сохраняет полученные строки в table
     */
    private void readJson() throws IOException {
        if (jsonParser.nextToken() != JsonToken.START_ARRAY) {
            throw new IllegalStateException("Ожидается массив по адресу " + jsonParser.currentLocation());
        }

        // Итерируется по всем объектам первичного массива
        while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
            // Парсит объект и вставляет несколько получившихся записей в таблицу
            readBrand();
        }
    }

    /**
     * Парсит объект бренда и вызывает readModel для каждой модели из массива models
     */
    private void readBrand() throws IOException {
        if (jsonParser.currentToken() != JsonToken.START_OBJECT) {
            throw new IllegalStateException("Ожидается объект по адресу " + jsonParser.currentLocation());
        }

        String brand = "";
        // Итерируется по свойствам
        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String property = jsonParser.getCurrentName();
            jsonParser.nextToken();
            switch (property) {
                case "brand":
                    brand = jsonParser.getText();
                    break;
                case "models":
                    if (brand.equals("")) {
                        throw new IllegalStateException("Ожидается непустое поле brand перед списком models");
                    }
                    // Заходит в массив и вызывает readModels для каждой модели
                    if (jsonParser.currentToken() != JsonToken.START_ARRAY) {
                        throw new IllegalStateException("Ожидается массив по адресу " + jsonParser.currentLocation());
                    }
                    // Итерируется по всем моделям
                    while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                        readModel(brand);
                    }
                    break;
                default:
                    throw new IllegalStateException("Неизвестное поле " + property +
                            ". Ожидается поле brand или models" + " по адресу " + jsonParser.currentLocation() );
            }
        }
    }

    /**
     * Парсит объект модели и для каждой обшивки из массива trims добавляет результирующую строку в таблицу table.
     * @param brand бренд обрабатываемой модели
     */
    private void readModel(String brand) throws IOException {
        if (jsonParser.currentToken() != JsonToken.START_OBJECT) {
            throw new IllegalStateException("Ожидается объект по адресу " + jsonParser.currentLocation());
        }
        String name = "";
        // Итерируется по свойствам
        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String property = jsonParser.getCurrentName();
            jsonParser.nextToken();

            switch (property) {
                case "name":
                    name = jsonParser.getText();
                    break;
                case "trims":
                    if (name.equals("")) {
                        throw new IllegalStateException("Ожидается непустое поле name перед списком trims");
                    }
                    // Заходит в массив и вызывает readModels для каждой модели
                    if (jsonParser.currentToken() != JsonToken.START_ARRAY) {
                        throw new IllegalStateException("Ожидается массив по адресу " + jsonParser.currentLocation());
                    }

                    while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                        String trim = readTrim();
                        table.add(new ArrayList<>(Arrays.asList(brand, name, trim)));
                    }
                    break;
                default:
                    throw new IllegalStateException("Неизвестное поле " + property +
                            ". Ожидается поле name или trims"+ " по адресу " + jsonParser.currentLocation());
            }
        }
    }

    /**
     * Парсит объект обшивки и возвращает значение поля name
     * @return Текущая обшивка
     */
    private String readTrim() throws IOException {
        String trim = "";
        if (jsonParser.currentToken() != JsonToken.START_OBJECT) {
            throw new IllegalStateException("Ожидается объект по адресу " + jsonParser.currentLocation());
        }
        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String property = jsonParser.getCurrentName();
            jsonParser.nextToken();

            // В объекте внутри массива trims должно быть поле name с непустым значением
            if (property.equals("name")) {
                trim = jsonParser.getText();
                if (trim.isBlank()) {
                    throw new IllegalStateException("Встречено пустое значение по адресу " +
                            jsonParser.currentLocation());
                }
            }
            else {
                throw new IllegalStateException("Неизвестное поле " + property +
                        ". Ожидается поле name" + " по адресу " + jsonParser.currentLocation());
            }
        }
        return trim;
    }
}
