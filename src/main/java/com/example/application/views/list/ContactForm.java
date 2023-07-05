package com.example.application.views.list;

import com.example.application.data.entity.Company;
import com.example.application.data.entity.Contact;
import com.example.application.data.entity.Status;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

import java.util.List;


public class ContactForm extends FormLayout {

    Binder<Contact> binder = new BeanValidationBinder<>(Contact.class);

    TextField firstName = new TextField("First name");
    TextField lastName = new TextField("Last name");
    TextField email = new TextField("Email");
    ComboBox<Status> status = new ComboBox<>("Status");
    ComboBox<Company> company = new ComboBox<>("Company");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button cancel = new Button("Cancel");
    private Contact contact;


    public ContactForm(List<Company> companyList, List<Status> statuses) {

        addClassName("contact-form");
        binder.bindInstanceFields(this);

        company.setItems(companyList);
        company.setItemLabelGenerator((ItemLabelGenerator<Company>) Company::getName);

        status.setItems(statuses);
        status.setItemLabelGenerator((ItemLabelGenerator<Status>) Status::getName);


        add(firstName, lastName, email, company, status, createButtonLayout());
    }

    private Component createButtonLayout() {

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        delete.addClickShortcut(Key.ESCAPE);


        save.addClickListener(buttonClickEvent -> validateAndSave());
        delete.addClickListener(buttonClickEvent -> displaySureToDeleteDialog());
        cancel.addClickListener(buttonClickEvent -> fireEvent(new CloseEvent(this)));


        return new HorizontalLayout(save, delete, cancel);
    }

    private void displaySureToDeleteDialog() {

        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.setHeader("Delete contact");
        confirmDialog.setText("Are you sure to delete " + contact.getFirstName() + " " + contact.getLastName() + "?");
        confirmDialog.addConfirmListener((ComponentEventListener<ConfirmDialog.ConfirmEvent>) confirmEvent -> fireEvent(new ContactForm.DeleteEvent(this, contact)));
        confirmDialog.setCancelable(true);

        Button confirmBtn = new Button("Confirm");
        confirmBtn.setClassName("delete-confirm");
        confirmDialog.setConfirmButton(confirmBtn);
        confirmDialog.open();
    }

    private void validateAndSave() {
        try {
            binder.writeBean(contact);
            fireEvent(new SaveEvent(this, contact));
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
    }


    public void setContact(Contact contact) {
        this.contact = contact;
        binder.readBean(contact);
    }

    // Events
    public static abstract class ContactFormEvent extends ComponentEvent<ContactForm> {
        private Contact contact;

        protected ContactFormEvent(ContactForm source, Contact contact) {
            super(source, false);
            this.contact = contact;
        }

        public Contact getContact() {
            return contact;
        }
    }

    public static class SaveEvent extends ContactFormEvent {
        SaveEvent(ContactForm source, Contact contact) {
            super(source, contact);
        }
    }

    public static class DeleteEvent extends ContactFormEvent {
        DeleteEvent(ContactForm source, Contact contact) {
            super(source, contact);
        }

    }

    public static class SureToDeleteEvent extends ContactFormEvent {
        SureToDeleteEvent(ContactForm source, Contact contact) {
            super(source, contact);
        }

    }

    public static class CloseEvent extends ContactFormEvent {
        CloseEvent(ContactForm source) {
            super(source, null);
        }
    }

    public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
        return addListener(DeleteEvent.class, listener);
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }

    public Registration addSureToDeleteListener(ComponentEventListener<SureToDeleteEvent> listener) {
        return addListener(SureToDeleteEvent.class, listener);
    }
}
