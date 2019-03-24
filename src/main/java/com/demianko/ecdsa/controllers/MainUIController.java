package com.demianko.ecdsa.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.util.ResourceBundle;

import com.demianko.ecdsa.controllers.util.Utility;
import com.demianko.ecdsa.logic.Stubs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

public class MainUIController implements Initializable {

	private Stage keyGenStage; // Stage of the keyGen window

	// ELEMENTS

	// Menu Bar

	@FXML
	private MenuBar menuBar;

	@FXML
	private MenuItem genKeysMenuItem;

	@FXML
	private MenuItem closeMenuItem;

	// Signing section

	@FXML
	private TextField fileToSignLocationField;
	@FXML
	private Button fileToSignBrowseButton;

	@FXML
	private TextField privateKeyLocationField;
	@FXML
	private Button privateKeyBrowseButton;

	@FXML
	private TextField signatureOutputLocationField;
	@FXML
	private Button signatureOutputBrowseButton;

	@FXML
	private Button signButton;

	// Verification section

	@FXML
	private TextField fileToVerifyLocationField;
	@FXML
	private Button fileToVerifyBrowseButton;

	@FXML
	private TextField publicKeyLocationField;
	@FXML
	private Button publicKeyBrowseButton;

	@FXML
	private TextField signatureFileLocationField;
	@FXML
	private Button signatureFileBrowseButton;

	@FXML
	private Button verifyButton;

	// Status bar

	@FXML
	private Label statusMessageLabel;
	private String defaultStatusMessageLabelStyle;

	// FILES TO WORK WITH

	private Window thisWindow; // Should be set on every action if null

	private File fileToSign;
	private File privateKey;
	private File signature;

	private File fileToVerify;
	private File publicKey;

	// HANDLERS

	@FXML
	private void handleClickOnBrowseButton(MouseEvent mouseEvent) {
		setWindowToReferTo();

		Button eventSource = (Button) mouseEvent.getSource(); // Get the source of the event
		FileChooser fileChooser = new FileChooser();

		// Set the corresponding TextField
		if (eventSource == fileToSignBrowseButton) {
			fileToSign = fileChooser.showOpenDialog(thisWindow);
			if (fileToSign != null) {
				fileToSignLocationField.setText(fileToSign.getAbsolutePath());
			}
		} else if (eventSource == privateKeyBrowseButton) {
			privateKey = fileChooser.showOpenDialog(thisWindow);
			if (privateKey != null) {
				privateKeyLocationField.setText(privateKey.getAbsolutePath());
			}
		} else if (eventSource == signatureOutputBrowseButton) {
			signature = fileChooser.showSaveDialog(thisWindow);
			if (signature != null) {
				signatureFileLocationField.setText(signature.getAbsolutePath());
			}
		} else if (eventSource == fileToVerifyBrowseButton) {
			fileToVerify = fileChooser.showOpenDialog(thisWindow);
			if (fileToVerify != null) {
				fileToVerifyLocationField.setText(fileToVerify.getAbsolutePath());
			}
		} else if (eventSource == publicKeyBrowseButton) {
			publicKey = fileChooser.showOpenDialog(thisWindow);
			if (publicKey != null) {
				publicKeyLocationField.setText(publicKey.getAbsolutePath());
			}
		} else if (eventSource == signatureFileBrowseButton) {
			signature = fileChooser.showOpenDialog(thisWindow);
			if (signature != null) {
				signatureFileLocationField.setText(signature.getAbsolutePath());
			}
		}
	}

	@FXML
	private void handleGenKeysMenuItemAction(ActionEvent actionEvent) {
		setWindowToReferTo();
		keyGenStage.show(); // Show window
	}

	@FXML
	private void handleCloseMenuItemAction(ActionEvent actionEvent) { // Close window
		setWindowToReferTo();
		((Stage) thisWindow).close();
	}

	@FXML
	private void handleClickOnSignButton(MouseEvent mouseEvent) {
		try {
			// Load the file to sign from the specified path
			fileToSign = Utility.loadIfExistsAndIsFile(fileToSignLocationField.getText(), "File to sign");
			// Load the private key from the specified path
			privateKey = Utility.loadIfExistsAndIsFile(privateKeyLocationField.getText(), "Private key");

			// If not specified, set to the current working directory path
			if (signatureOutputLocationField.getText().compareTo("") == 0) {
				// Default output filename = fileToSign.getName() + ".signature"
				File tempFile = new File(System.getProperty("user.dir"), fileToSign.getName() + ".signature");
				if (tempFile.exists()) {
					throw new FileAlreadyExistsException(tempFile.getCanonicalPath()); // Throw if default output file
																						// already exists
				}
				signatureOutputLocationField.setText(tempFile.getAbsolutePath());
			}

			signature = new File(signatureOutputLocationField.getText());

			Stubs.sign(fileToSign, privateKey, signature); // Stub
			setLabelTextSuccess(String.format("File %s was successfully signed!", fileToSign.getName()));
		} catch (Exception e) {
			setLabelTextFailure(e.getMessage());
		}
	}

	@FXML
	private void handleClickOnVerifyButton(MouseEvent mouseEvent) {
		try {
			// Load the file to verify from the specified path
			fileToVerify = Utility.loadIfExistsAndIsFile(fileToVerifyLocationField.getText(), "File to verify");
			// Load the public key from the specified path
			publicKey = Utility.loadIfExistsAndIsFile(publicKeyLocationField.getText(), "Public key");
			// Load the signature from the specified path
			signature = Utility.loadIfExistsAndIsFile(signatureFileLocationField.getText(), "Signature file");

			Stubs.verify(fileToVerify, publicKey, signature); // Stub
			setLabelTextSuccess(String.format("File %s was successfully verified!", fileToSign.getName()));
		} catch (Exception e) {
			setLabelTextFailure(e.getMessage());
		}
	}

	// UTILITY METHODS

	private void setWindowToReferTo() { // Set variable to refer to it later
		if (thisWindow == null) {
			thisWindow = menuBar.getScene().getWindow();
		}
	}

	private void setDefaultLabelStyle() {
		statusMessageLabel.setStyle(defaultStatusMessageLabelStyle);
	}

	void setLabelTextSuccess(String message) { // Message on success
		setDefaultLabelStyle();
		statusMessageLabel.setStyle("-fx-text-fill: green");
		statusMessageLabel.setText(message);
	}

	void setLabelTextFailure(String message) { // Message on failure
		setDefaultLabelStyle();
		statusMessageLabel.setStyle("-fx-text-fill: red");
		statusMessageLabel.setText(message);
	}

	// INITIALIZATION

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		defaultStatusMessageLabelStyle = statusMessageLabel.getStyle(); // Remember the default status message style

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("ui_keygen.fxml"));

		Parent keyGenParent = null;
		try {
			keyGenParent = fxmlLoader.load(); // Load the keyGen window layout
		} catch (IOException e) {
			System.exit(1); // Exit if layout failed to load
		}

		fxmlLoader.<KeyGenUIController>getController().setMainUIController(this); // Load controller

		Scene scene = new Scene(keyGenParent);
		keyGenStage = new Stage();
		keyGenStage.initOwner(thisWindow);
		keyGenStage.setTitle("Key generation menu");
		keyGenStage.setScene(scene);

		// Finish initialisation by providing status
		statusMessageLabel.setText("Ready");
	}
}