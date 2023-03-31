import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        System.out.println("Client är nu redo");


        //Init stuff. Set as null to be initialized as "something"
        Socket socket = null;
        InputStreamReader inputSR = null;
        OutputStreamWriter outputSW = null;
        BufferedReader bReader = null;
        BufferedWriter bWriter = null;

        //Starta Klienten
        try {
            //Init Socket med specifik port
            socket = new Socket("localhost", 4321);

            //Initiera Reader och Writer och koppla dem till socket
            inputSR = new InputStreamReader(socket.getInputStream());
            outputSW = new OutputStreamWriter(socket.getOutputStream());
            bReader = new BufferedReader(inputSR);
            bWriter = new BufferedWriter(outputSW);

            //Initiera Scanner för att skriva i konsol
            Scanner scan = new Scanner(System.in);

            while (true) {
                String[] message = userInput();

                //Skicka meddelande till server
                bWriter.write(message[1]);
                bWriter.newLine();
                bWriter.flush();

                //Hämta response från server
                String resp = bReader.readLine();

                JSONParser parser = new JSONParser();


                //Hämtar response från server och skapa JSON objekt
                JSONObject serverResponse = (JSONObject) parser.parse(resp);

                //Kollar om respons lyckas
                if ("200".equals(serverResponse.get("httpStatusCode").toString())) {

                    //Bygger upp ett JSONObject av den returnerade datan
                    JSONObject data = (JSONObject) parser.parse((String) serverResponse.get("data"));


                    //Hämtar en lista av alla nyckel attribut i data och loopar sedan genom dem.
                    //Lägger till if statement som väljer olika värden beroende på användarens input
                    Set<String> keys =  data.keySet();


                    for (String x : keys) {

                        JSONObject person = (JSONObject) data.get(x);

                        if (message[0].equals("1") ) {
                            System.out.println(person.get("name"));
                        } else if (message[0].equals("2")) {
                            System.out.println(person.get("age"));
                        } else if (message[0].equals("3")) {
                            System.out.println(person.get("favoriteColor"));

                        } else if (message[0].equals("4")) {
                            System.out.println(person.get("favoriteMovie"));
                        } else if(message[0].equals("5")) {
                           System.exit(0);
                        }


                    }

                }

            }

        } catch (UnknownHostException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        } catch (ParseException e) {
            System.out.println(e);
        } finally {
            try {
                //Stäng kopplingar
                if (socket != null ) socket.close();
                if (inputSR != null ) inputSR.close();
                if (outputSW != null ) outputSW.close();
                if (bWriter != null ) bWriter.close();
                if (bReader != null ) bReader.close();
            } catch (Exception e) {
                System.out.println(e);
            }
            System.out.println("Client Avslutas");
        }
    }
    static String[] userInput() {
        //Steg 1. Skriv ut en meny för användaren
        System.out.println("1. Hämta namn på alla personer");
        System.out.println("2. Hämta ålder för alla personer");
        System.out.println("3. Hämta favorit färg för alla personer");
        System.out.println("4. Hämta favorit film för alla personer");
        System.out.println("5. Avsluta");
        //Steg 2. Låta användaren göra ett val
        Scanner scan = new Scanner(System.in);
        System.out.println("Skriv in ditt menyval: ");

        String choice = scan.nextLine();

        //Steg 3. Bearbeta användarens val

                //Skapa JSON objekt för att hämta data om alla personer. Stringifiera objektet och returnera det
                JSONObject jsonReturn = new JSONObject();
               jsonReturn.put("httpURL", "persons");
                jsonReturn.put("httpMethod", "get");

                String[] returnedData = {choice, jsonReturn.toJSONString()};
        //Returnera JSON objekt
                return returnedData;
    }

}