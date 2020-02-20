package bg.sofia.uni.fmi.mjt.food.analyzer.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import bg.sofia.uni.fmi.mjt.food.analyzer.server.api.APIConnector;
import bg.sofia.uni.fmi.mjt.food.analyzer.server.barcode.BarcodeReader;

public class RequestHandler implements Runnable{
	
	private static final String CODE_ATTRIBUTE_REGEX = "--code=.*";
	private static final String IMG_ATTRIBUTE_REGEX = "--img=.*";
	
	private Socket socket;
	private APIConnector apiConnector;

	public RequestHandler(Socket socket) {
		this.socket = socket;
		apiConnector = new APIConnector();
	}

	@Override
	public void run() {

		try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

			String inputLine;

			while ((inputLine = in.readLine()) != null) {

				System.out.println("Message received from client: " + inputLine);

				if (inputLine.split(" ")[0].equalsIgnoreCase("get-food")) {
					try {
						if (!inputLine.contains(" ")) {
							out.println("Command must be followed by food name!");
						} else if (inputLine.split(" ", 2)[1].isEmpty()) {
							out.println("Command must be followed by food name!");
						} else {
							out.println(apiConnector.printFoodInfo(inputLine.split(" ", 2)[1]));
						}
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				} else if (inputLine.split(" ")[0].equalsIgnoreCase("get-food-report")) {
					try {
						if (!inputLine.contains(" ")) {
							out.println("Command must be followed by food id!");
						} else if (inputLine.split(" ", 2)[1].isEmpty()) {
							out.println("Command must be followed by food id!");
						} else {
							out.println(apiConnector.printFoodReportInfo(inputLine.split(" ", 2)[1]));
						}
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				} else if (inputLine.split(" ")[0].equalsIgnoreCase("get-food-by-barcode")) {
					try {
						if (!inputLine.contains(" ")) {
							out.println("Command must be followed by code or barcode image!");
						} else if (inputLine.split(" ")[1].isEmpty()) {
							out.println("Command must be followed by code or barcode image!");
						} else if (inputLine.split(" ", 3)[1].matches(CODE_ATTRIBUTE_REGEX)) {
							String code = inputLine.split(" ", 2)[1].split("=")[1];
							out.println(apiConnector.printFoodInfoByBarcode("gtinUpc: " + code));
							/*
							 * } else if (inputLine.split(" ", 3)[2].matches(CODE_ATTRIBUTE_REGEX)) { String
							 * code = inputLine.split(" ", 3)[2].split("=")[1];
							 * out.println(apiConnector.printFoodInfoByBarcode("gtinUpc: " + code));
							 */
						} else if (inputLine.split(" ")[1].matches(IMG_ATTRIBUTE_REGEX)) {
							String pathToImage = inputLine.split(" ")[1].split("=")[1];
							String code = BarcodeReader.getBarcode(pathToImage);
							out.println(apiConnector.printFoodInfoByBarcode("gtinUpc: " + code));
						} else {
							out.println("Invalid parameters!");
						}
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}

				} else {
					out.println("Unrecognized command!");
				}

			}

		} catch (IOException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
            }
        }

    }
}
