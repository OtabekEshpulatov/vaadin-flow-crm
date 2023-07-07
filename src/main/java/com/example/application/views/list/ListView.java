package com.example.application.views.list;

import com.example.application.data.entity.Contact;
import com.example.application.data.service.CRMService;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;


@PageTitle("Contacts | Vaadin CRM")
@Route(value = "", layout = MainLayout.class)
@PermitAll
public class ListView extends VerticalLayout {


    private final CRMService service;
    Grid<Contact> grid = new Grid<>(Contact.class);
    TextField filterText = new TextField();
    ContactForm contactForm;


    public ListView(CRMService service) {
        this.service = service;

        addClassName("list-view");

        setSizeFull();

        configureGrid();
        configureForm();

        add(getToolBar(), getContent());

        updateList();
        closeEditor();
    }

    private void closeEditor() {
        contactForm.setContact(null);
        contactForm.setVisible(false);
        removeClassName("editing");
    }

    private void updateList() {
        grid.setItems(service.findAllContacts(filterText.getValue()));
    }

    private Component getContent() {

        HorizontalLayout content = new HorizontalLayout(grid, contactForm);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, contactForm);
        content.addClassName("content");
        content.setSizeFull();
        return content;

    }

    private void configureForm() {

        contactForm = new ContactForm(service.findAllCompanies(), service.findAllStatuses());
        contactForm.setWidth("25em");


        contactForm.addSaveListener((ComponentEventListener<ContactForm.SaveEvent>) this::saveContact);
        contactForm.addDeleteListener((ComponentEventListener<ContactForm.DeleteEvent>) this::deleteContact);
        contactForm.addCloseListener((ComponentEventListener<ContactForm.CloseEvent>) closeEvent -> closeEditor());
    }


    private void deleteContact(ContactForm.DeleteEvent deleteEvent) {
        service.deleteContact(deleteEvent.getContact());
        updateList();
        closeEditor();
    }


    private void saveContact(ContactForm.SaveEvent event) {
        service.saveContact(event.getContact());
        updateList();
        closeEditor();

    }

    private Component getToolBar() {
        filterText.setPlaceholder("Filter by name... ");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener((HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<TextField, String>>) textFieldStringComponentValueChangeEvent -> updateList());

        Button addContactBtn = new Button("Add contact");
        addContactBtn.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> displayAddContactView());

        HorizontalLayout toolBar = new HorizontalLayout(filterText, addContactBtn);
        toolBar.addClassName("toolbar");
        return toolBar;
    }

    private void displayAddContactView() {


        Dialog addContactDialog = new Dialog();

        AddContactForm addContactForm = new AddContactForm(service.findAllCompanies(), service.findAllStatuses());
        addContactForm.addCloseListener((ComponentEventListener<AddContactForm.CloseEvent>) closeEvent -> addContactDialog.close());
        addContactForm.addSaveListener((ComponentEventListener<AddContactForm.SaveEvent>) saveEvent -> {
            saveContact(saveEvent);
            addContactDialog.close();
        });

        addContactDialog.add(addContactForm);

        addContactDialog.setHeaderTitle("New Contact Form");
        addContactDialog.setWidth("40em");
        addContactDialog.setHeight("40em");
        addContactDialog.setCloseOnOutsideClick(false);
        addContactDialog.open();


    }

    private void saveContact(AddContactForm.SaveEvent saveEvent) {
        service.saveContact(saveEvent.getContact());
        updateList();
    }

    private void configureGrid() {

        grid.addClassName("contact-grid");
        grid.setSizeFull();
        grid.setColumns("firstName", "lastName", "email");
        grid.addColumn(contact -> contact.getStatus().getName()).setHeader("Status");
        grid.addColumn(contact -> contact.getCompany().getName()).setHeader("Company");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(e -> editContact(e.getValue()));


    }

    private void editContact(Contact contact) {

        if (contact == null) {
            closeEditor();
        } else {
            contactForm.setContact(contact);
            contactForm.setVisible(true);
            contactForm.addClassName("editing");
        }
    }

}
