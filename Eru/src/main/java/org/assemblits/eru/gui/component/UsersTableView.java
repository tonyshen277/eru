package org.assemblits.eru.gui.component;

import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import org.assemblits.eru.entities.User;

public class UsersTableView extends EruTableView<User> {

    public UsersTableView(){
        TableColumn<User, String> groupColumn = new TableColumn<>("Group");
        TableColumn<User, String> userNameColumn = new TableColumn<>("Username");
        TableColumn<User, String> firstNameColumn = new TableColumn<>("First name");
        TableColumn<User, String> lastNameColumn = new TableColumn<>("Last name");
        TableColumn<User, String> emailColumn = new TableColumn<>("Email");
        TableColumn<User, String> passwordColumn = new TableColumn<>("Password");
        TableColumn<User, Boolean> onlineColumn = new TableColumn<>("Online");

        groupColumn.setCellValueFactory(param -> param.getValue().groupNameProperty());
        groupColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        groupColumn.prefWidthProperty().bind(widthProperty().multiply(0.14));

        userNameColumn.setCellValueFactory(param -> param.getValue().userNameProperty());
        userNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        userNameColumn.prefWidthProperty().bind(widthProperty().multiply(0.15));

        firstNameColumn.setCellValueFactory(param -> param.getValue().firstNameProperty());
        firstNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        firstNameColumn.prefWidthProperty().bind(widthProperty().multiply(0.15));

        lastNameColumn.setCellValueFactory(param -> param.getValue().lastNameProperty());
        lastNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        lastNameColumn.prefWidthProperty().bind(widthProperty().multiply(0.14));

        emailColumn.setCellValueFactory(param -> param.getValue().emailProperty());
        emailColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        emailColumn.prefWidthProperty().bind(widthProperty().multiply(0.14));

        passwordColumn.setCellValueFactory(param -> param.getValue().passwordProperty());
        passwordColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        passwordColumn.prefWidthProperty().bind(widthProperty().multiply(0.14));

        onlineColumn.setCellValueFactory(param -> param.getValue().onlineProperty());
        onlineColumn.prefWidthProperty().bind(widthProperty().multiply(0.14));
        onlineColumn.setCellFactory(CheckBoxTableCell.forTableColumn(onlineColumn));

        getColumns().addAll(
                groupColumn,
                userNameColumn,
                firstNameColumn,
                lastNameColumn,
                emailColumn,
                passwordColumn,
                onlineColumn);

        setEditable(true);
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @Override
    public void addNewItem() {
        User newUser = new User();
        newUser.setUserName("newUser");
        newUser.setGroupName("Users");
        getItems().add(newUser);
        getSelectionModel().clearSelection();
        getSelectionModel().select(newUser);
    }

    public void setUsers(ObservableList<User> users) {
        super.setItems(users);
    }
}
