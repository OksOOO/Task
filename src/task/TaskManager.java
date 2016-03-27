/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package task;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;
import task.model.Person;

import com.sun.prism.impl.Disposer.Record;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author oksana
 */
public class TaskManager extends Application {
    
    private final TableView table = new TableView();
    final HBox hb = new HBox();
    private final ObservableList<Person> data = FXCollections.observableArrayList(PersonSB.getPersons());
    
    @Override
    public void start(Stage stage) { 
        Scene scene = new Scene(new Group());
        stage.setTitle("Table of persons");
        stage.setWidth(505);
        stage.setHeight(550);
 
        final Label label = new Label("Persons");
        label.setFont(new Font("Arial", 20));
 
        table.setEditable(true);
        
        Callback<TableColumn<Record, String>, 
            TableCell<Record, String>> cellFactory
                = (TableColumn<Record, String> p) -> new EditingCell();
 
        TableColumn<Record, String> firstNameCol = 
            new TableColumn<>("First Name");
        TableColumn<Record, String> lastNameCol = 
            new TableColumn<>("Last Name");
        TableColumn<Record, String> emailCol = 
            new TableColumn<>("Email");
 
        firstNameCol.setMinWidth(100);
        firstNameCol.setCellValueFactory(
            new PropertyValueFactory<>("firstName"));
        firstNameCol.setCellFactory(cellFactory);        
        firstNameCol.setOnEditCommit(
            (CellEditEvent<Record, String> t) -> {
                Person person = ((Person) t.getTableView().getItems().get(
                        t.getTablePosition().getRow()));
                person.setFirstName(t.getNewValue());
                PersonSB.updatePerson(person.getId(), person.getFirstName(), person.getLastName(), person.getEmail());
        });
 
        lastNameCol.setMinWidth(100);
        lastNameCol.setCellValueFactory(
            new PropertyValueFactory<>("lastName"));
        lastNameCol.setCellFactory(cellFactory);
        lastNameCol.setOnEditCommit(
            (CellEditEvent<Record, String> t) -> {
                Person person = ((Person) t.getTableView().getItems().get(
                        t.getTablePosition().getRow()));
                person.setLastName(t.getNewValue());
                PersonSB.updatePerson(person.getId(), person.getFirstName(), person.getLastName(), person.getEmail());
        });
 
        emailCol.setMinWidth(200);
        emailCol.setCellValueFactory(
            new PropertyValueFactory<>("email"));
        emailCol.setCellFactory(cellFactory);
        emailCol.setOnEditCommit(
            (CellEditEvent<Record, String> t) -> {
                Person person = ((Person) t.getTableView().getItems().get(
                        t.getTablePosition().getRow()));
                person.setEmail(t.getNewValue());
                PersonSB.updatePerson(person.getId(), person.getFirstName(), person.getLastName(), person.getEmail());
        });
        
        //Insert Button
        TableColumn col_action = new TableColumn<>("Action");       
        col_action.setCellValueFactory(
            new Callback<TableColumn.CellDataFeatures<Record, Boolean>, 
            ObservableValue<Boolean>>() {
                @Override
                public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Record, Boolean> p) {
                    return new SimpleBooleanProperty(p.getValue() != null);
                }
        });
        //Adding the Button to the cell
        col_action.setCellFactory(
            new Callback<TableColumn<Record, Boolean>, TableCell<Record, Boolean>>() {
                @Override
                public TableCell<Record, Boolean> call(TableColumn<Record, Boolean> p) {
                    return new DeleteButtonCell();
                }       
        });
 
        table.setItems(data);
        table.getColumns().addAll(firstNameCol, lastNameCol, emailCol, col_action);
 
        final TextField addFirstName = new TextField();
        addFirstName.setPromptText("First Name");
        addFirstName.setMaxWidth(firstNameCol.getPrefWidth());
        final TextField addLastName = new TextField();
        addLastName.setMaxWidth(lastNameCol.getPrefWidth());
        addLastName.setPromptText("Last Name");
        final TextField addEmail = new TextField();
        addEmail.setMaxWidth(emailCol.getPrefWidth());
        addEmail.setPromptText("Email");
 
        final Button addButton = new Button("Add");
        addButton.setOnAction((ActionEvent e) -> {
            data.add(new Person(
                    addFirstName.getText(),
                    addLastName.getText(),
                    addEmail.getText()));
            PersonSB.savePerson(addFirstName.getText(), addLastName.getText(), addEmail.getText());
            addFirstName.clear();
            addLastName.clear();
            addEmail.clear();
        });
 
        hb.getChildren().addAll(addFirstName, addLastName, addEmail, addButton);
        hb.setSpacing(3);
 
        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, table, hb);
 
        ((Group) scene.getRoot()).getChildren().addAll(vbox);
 
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    //Define the button cell
    private class DeleteButtonCell extends TableCell<Record, Boolean> {
        final Button cellButton = new Button("Delete");
        
        DeleteButtonCell(){
            //Action when the button is pressed
            cellButton.setOnAction(new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent t) {
                    // get Selected Item
                    Person currentPerson = (Person) DeleteButtonCell.this.getTableView().getItems().get(DeleteButtonCell.this.getIndex());
                    //remove selected item from the table list
                    data.remove(currentPerson);
                    PersonSB.deletePerson(currentPerson.getId());
                }
            });
        }

        //Display button if the row is not empty
        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);
            if(!empty){
                setGraphic(cellButton);
            } else{
                setGraphic(null);
            }
        }
    }
    
}
