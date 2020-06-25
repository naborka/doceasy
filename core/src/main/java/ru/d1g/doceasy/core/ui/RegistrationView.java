package ru.d1g.doceasy.core.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import ru.d1g.doceasy.core.service.iface.AccountService;
import ru.d1g.doceasy.core.service.iface.RegistrationService;
import ru.d1g.doceasy.postgres.model.RegistrationRequest;

@Route(RegistrationView.ROUTE)
@PageTitle("Регистрация")
public class RegistrationView extends VerticalLayout {
    public static final String ROUTE = "registration";

    private PasswordField passwordField1;
    private PasswordField passwordField2;

    private final AccountService accountService;
    private final RegistrationService registrationService;
    private BeanValidationBinder<RegistrationRequest> binder;

    /**
     * Flag for disabling first run for password validation
     */
    private boolean enablePasswordValidation;

    /**
     * We use Spring to inject the backend into our view
     *
     * @param accountService
     * @param registrationService
     */
    public RegistrationView(AccountService accountService, RegistrationService registrationService) {
        this.accountService = accountService;
        this.registrationService = registrationService;

        /*
         * Create the components we'll need
         */

        H3 title = new H3("Регистрация");

        TextField personalName = new TextField("Имя");
        EmailField emailField = new EmailField("Логин (E-mail)");

        passwordField1 = new PasswordField("Пароль");
        passwordField2 = new PasswordField("Повтор пароля");

        Span errorMessage = new Span();

        Button submitButton = new Button("Зарегистрироваться");
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        /*
         * Build the visible layout
         */
        VerticalLayout verticalLayout = new VerticalLayout(emailField, passwordField1, passwordField2, submitButton);

        // Create a FormLayout with all our components. The FormLayout doesn't have any
        // logic (validation, etc.), but it allows us to configure Responsiveness from
        // Java code and its defaults looks nicer than just using a VerticalLayout.
        FormLayout formLayout = new FormLayout(title, verticalLayout, errorMessage);

        // Restrict maximum width and center on page
        formLayout.setMaxWidth("500px");
        formLayout.getStyle().set("margin", "0 auto");

        // Allow the form layout to be responsive. On device widths 0-490px we have one
        // column, then we have two. Field labels are always on top of the fields.
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("490px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));

        // These components take full width regardless if we use one column or two (it
        // just looks better that way)
        formLayout.setColspan(title, 2);
        formLayout.setColspan(errorMessage, 2);
        formLayout.setColspan(submitButton, 2);

        // Add some styles to the error message to make it pop out
        errorMessage.getStyle().set("color", "var(--lumo-error-text-color)");
        errorMessage.getStyle().set("padding", "15px 0");

        // Add the form to the page
        add(formLayout);

        /*
         * Set up form functionality
         */

        /*
         * Binder is a form utility class provided by Vaadin. Here, we use a specialized
         * version to gain access to automatic Bean Validation (JSR-303). We provide our
         * data class so that the Binder can read the validation definitions on that
         * class and create appropriate validators. The BeanValidationBinder can
         * automatically validate all JSR-303 definitions, meaning we can concentrate on
         * custom things such as the passwords in this class.
         */
        binder = new BeanValidationBinder<>(RegistrationRequest.class);

        // The handle has a custom validator, in addition to being required. Some values
        // are not allowed, such as 'admin'; this is checked in the validator.
        binder.forField(emailField).withValidator(this::validateHandle).withValidator(new VisibilityEmailValidator("Value is not a valid email address")).asRequired().bind("email");

        // Another custom validator, this time for passwords
        binder.forField(passwordField1).asRequired().withValidator(this::passwordValidator).bind("password");
        // We won't bind passwordField2 to the Binder, because it will have the same
        // value as the first field when correctly filled in. We just use it for
        // validation.

        binder.forField(personalName).bind("personalName");

        // The second field is not connected to the Binder, but we want the binder to
        // re-check the password validator when the field value changes. The easiest way
        // is just to do that manually.
        passwordField2.addValueChangeListener(e -> {

            // The user has modified the second field, now we can validate and show errors.
            // See passwordValidator() for how this flag is used.
            enablePasswordValidation = true;

            binder.validate();
        });

        // A label where bean-level error messages go
        binder.setStatusLabel(errorMessage);

        // And finally the submit button
        submitButton.addClickListener(e -> {
            try {

                // Create empty bean to store the details into
                RegistrationRequest registrationRequest = new RegistrationRequest();

                // Run validators and write the values to the bean
                binder.writeBean(registrationRequest);

                // Call backend to store the data
                RegistrationRequest request = this.registrationService.request(registrationRequest);

                // Show success message if everything went well
                showSuccess(request);

            } catch (ValidationException ve) {
                // validation errors are already visible for each field,
                // and bean-level errors are shown in the status label.

                // We could show additional messages here if we want, do logging, etc.

                ve.printStackTrace();

                // Notify, and let the user try again.
                errorMessage.setText("Ошибка");
            }
        });

    }

    /**
     * We call this method when form submission has succeeded
     */
    private void showSuccess(RegistrationRequest registrationRequest) {
        Notification notification = Notification.show("На указанный email выслано письмо подтверждения регистрации");
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        // Here you'd typically redirect the user to another view
    }

    /**
     * Method to validate that:
     * <p>
     * 1) Password is at least 8 characters long
     * <p>
     * 2) Values in both fields match each other
     */
    private ValidationResult passwordValidator(String pass1, ValueContext ctx) {
        if (pass1 == null || pass1.length() < 5) {
            return ValidationResult.error("Password should be at least 8 characters long");
        }

        if (!enablePasswordValidation) {
            enablePasswordValidation = true;
            return ValidationResult.ok();
        }

        String pass2 = passwordField2.getValue();

        if (pass1.equals(pass2)) {
            return ValidationResult.ok();
        }

        return ValidationResult.error("Пароли не совпадают");
    }

    /**
     * Method that demonstrates using an external validator. Here we ask the backend
     * if this handle is already in use.
     */
    private ValidationResult validateHandle(String handle, ValueContext ctx) {
        boolean identityExists = accountService.isIdentityAlreadyExists(handle);
        if (identityExists) {
            boolean confirmed = accountService.findIdentity(handle).isConfirmed();
            if (confirmed) {
                return ValidationResult.error("Такой пользоваель уже существует");
            }
        }
        return ValidationResult.ok();
    }

    /**
     * Custom validator class that extends the built-in email validator.
     * <p>
     * Ths validator checks if the field is visible before performing the
     * validation. This way, the validation is only performed when the user has told
     * us they want marketing emails.
     */
    public class VisibilityEmailValidator extends EmailValidator {
        public VisibilityEmailValidator(String errorMessage) {
            super(errorMessage);
        }

        @Override
        public ValidationResult apply(String value, ValueContext context) {
            if (true) {
                return ValidationResult.ok();
            } else {
                return super.apply(value, context);
            }
        }
    }
}

