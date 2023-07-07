package com.example.application.views.list;

import com.example.application.data.entity.Company;
import com.example.application.data.entity.Contact;
import com.example.application.data.entity.Status;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;


import java.util.List;


public class AddContactForm extends FormLayout {


    TextField firstName = new TextField("First name");
    TextField lastName = new TextField("Last name");
    TextField email = new TextField("Email");
    ComboBox<Status> status = new ComboBox<>("Status");
    ComboBox<Company> company = new ComboBox<>("Company");

    Binder<Contact> contactBinder = new BeanValidationBinder<>(Contact.class);

    Button save = new Button("Save");
    Button cancel = new Button("Cancel");
    private Contact contact;


    public AddContactForm(List<Company> companyList, List<Status> statuses) {


        bindContact();
        addClassName("contact-form");

        company.setItems(companyList);
        company.setItemLabelGenerator((ItemLabelGenerator<Company>) Company::getName);

        status.setItems(statuses);
        status.setItemLabelGenerator((ItemLabelGenerator<Status>) Status::getName);


        add(firstName, lastName, email, company, status, createButtonLayout());
    }

    private void bindContact() {

        if (contact == null) {
            contact = new Contact();
        }
        contactBinder.bindInstanceFields(this);
        contactBinder.readBean(contact);
    }


    private Component createButtonLayout() {

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);


        save.addClickListener(buttonClickEvent -> validateAndSave());
        cancel.addClickListener(buttonClickEvent -> fireEvent(new CloseEvent(this)));


        return new HorizontalLayout(save, cancel);
    }


    private void validateAndSave() {
        try {
            contactBinder.writeBean(contact);
            fireEvent(new SaveEvent(this, contact));
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
    }



    public static abstract class ContactFormEvent extends ComponentEvent<AddContactForm> {
        private Contact contact;

        protected ContactFormEvent(AddContactForm source, Contact contact) {
            super(source, false);
            this.contact = contact;
        }

        public Contact getContact() {
            return contact;
        }
    }

    public static class SaveEvent extends ContactFormEvent {
        SaveEvent(AddContactForm source, Contact contact) {
            super(source, contact);
        }
    }


    public static class CloseEvent extends ContactFormEvent {
        CloseEvent(AddContactForm source) {
            super(source, null);
        }
    }


    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }

}
