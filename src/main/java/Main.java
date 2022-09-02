import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, "data.json");
        List<Employee> list1 = parseXML("data.xml");
        System.out.println(list1);
        String json2 = listToJson(list1);
        writeString(json2, "data2.json");
    }

    public static String returnData(){
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        Calendar calendar = new GregorianCalendar();
        return formatter.format(calendar.getTime());
    }

    public static List<Employee> parseCSV(String[] columnExample, String fileExample){
        List<Employee> resultFromCsv = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(fileExample))){
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();

            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnExample);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            resultFromCsv = csv.parse();
            System.out.println("Десериализация из файла была успешно завершена в " + returnData());
            return resultFromCsv;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultFromCsv;
    }

    public static List<Employee> parseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newDefaultInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));

        Node root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();

        List<Employee> listOfEmployees = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++){
            Node node_ = nodeList.item(i);
            if (Node.ELEMENT_NODE == node_.getNodeType()){
                Element element = (Element) node_;
                Employee employee = new Employee();
                employee.id = Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent());
                employee.firstName =  element.getElementsByTagName("firstName").item(0).getTextContent();
                employee.lastName = element.getElementsByTagName("lastName").item(0).getTextContent();
                employee.country = element.getElementsByTagName("country").item(0).getTextContent();
                employee.age = Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent());
                listOfEmployees.add(employee);
            }
        }
        System.out.println("Десериализация из файла " + fileName + " успешно завершена в " + returnData());
        return listOfEmployees;
    }

    public static String listToJson(List<Employee> employeeList){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        String json = gson.toJson(employeeList, listType);
        System.out.println("Список был преобразован в строчку в формате JSON в " + returnData());
        return json;
    }

    public static void writeString (String json, String fileName){
        try (FileWriter file = new FileWriter(fileName)){
            file.write(json);
            file.flush();
            System.out.println("Файл был записан в " + fileName + " в " + returnData());
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
