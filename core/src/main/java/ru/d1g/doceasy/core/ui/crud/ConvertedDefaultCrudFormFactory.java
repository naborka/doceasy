package ru.d1g.doceasy.core.ui.crud;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.converter.LocalDateToDateConverter;
import com.vaadin.flow.data.converter.StringToBigDecimalConverter;
import com.vaadin.flow.data.converter.StringToBigIntegerConverter;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.data.converter.StringToFloatConverter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.converter.StringToLongConverter;
import org.vaadin.crudui.crud.CrudOperation;
import org.vaadin.crudui.form.CrudFormConfiguration;
import org.vaadin.crudui.form.impl.form.factory.DefaultCrudFormFactory;
import org.vaadin.data.converter.StringToByteConverter;
import org.vaadin.data.converter.StringToCharacterConverter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

public class ConvertedDefaultCrudFormFactory<T> extends DefaultCrudFormFactory<T> {
    public ConvertedDefaultCrudFormFactory(Class<T> domainType) {
        super(domainType);
    }

    public ConvertedDefaultCrudFormFactory(Class<T> domainType, FormLayout.ResponsiveStep... responsiveSteps) {
        super(domainType, responsiveSteps);
    }

    @Override
    protected void bindField(HasValue field, String property, Class<?> propertyType) {
        Binder.BindingBuilder bindingBuilder = binder.forField(field);

        if (TextField.class.isAssignableFrom(field.getClass()) || PasswordField.class.isAssignableFrom(field.getClass())
                || TextArea.class.isAssignableFrom(field.getClass())) {
            bindingBuilder = bindingBuilder.withNullRepresentation("");
        }

        if (Double.class.isAssignableFrom(propertyType) || double.class.isAssignableFrom(propertyType)) {
            bindingBuilder = bindingBuilder.withConverter(new StringToDoubleConverter(null, "Must be a number"));

        } else if (Long.class.isAssignableFrom(propertyType) || long.class.isAssignableFrom(propertyType)) {
            bindingBuilder = bindingBuilder.withConverter(new StringToLongConverter(null, "Must be a number"));

        } else if (BigDecimal.class.isAssignableFrom(propertyType)) {
            bindingBuilder = bindingBuilder.withConverter(new StringToBigDecimalConverter(null, "Must be a number"));

        } else if (BigInteger.class.isAssignableFrom(propertyType)) {
            bindingBuilder = bindingBuilder.withConverter(new StringToBigIntegerConverter(null, "Must be a number"));

        } else if (Integer.class.isAssignableFrom(propertyType) || int.class.isAssignableFrom(propertyType)) {
            bindingBuilder = bindingBuilder.withConverter(new StringToIntegerConverter(null, "Must be a number"));

        } else if (Byte.class.isAssignableFrom(propertyType) || byte.class.isAssignableFrom(propertyType)) {
            bindingBuilder = bindingBuilder.withConverter(new StringToByteConverter(null, "Must be a number"));

        } else if (Character.class.isAssignableFrom(propertyType) || char.class.isAssignableFrom(propertyType)) {
            bindingBuilder = bindingBuilder.withConverter(new StringToCharacterConverter());

        } else if (Float.class.isAssignableFrom(propertyType) || float.class.isAssignableFrom(propertyType)) {
            bindingBuilder = bindingBuilder.withConverter(new StringToFloatConverter(null, "Must be a number"));

        } else if (Date.class.isAssignableFrom(propertyType)) {
            bindingBuilder = bindingBuilder.withConverter(new LocalDateToDateConverter());
        }


        Optional<Map.Entry<CrudOperation, CrudFormConfiguration>> entry = configurations.entrySet().stream().findFirst();
        if (entry.isPresent()) {
            Converter<?, ?> converter = entry.get().getValue().getConverters().get(property);
            if (converter != null) {
                bindingBuilder.withConverter(converter);
            }
        }

        bindingBuilder.bind(property);
    }
}
