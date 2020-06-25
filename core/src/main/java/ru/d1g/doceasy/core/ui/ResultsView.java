package ru.d1g.doceasy.core.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.router.Route;
import org.vaadin.crudui.crud.CrudOperation;
import org.vaadin.crudui.form.impl.form.factory.DefaultCrudFormFactory;
import org.vaadin.crudui.layout.impl.VerticalSplitCrudLayout;
import ru.d1g.doceasy.core.service.iface.ImageService;
import ru.d1g.doceasy.core.service.iface.ResultService;
import ru.d1g.doceasy.core.service.iface.TaskJobService;
import ru.d1g.doceasy.core.ui.crud.ConvertedDefaultCrudFormFactory;
import ru.d1g.doceasy.core.ui.crud.GridCrudEnchanted;
import ru.d1g.doceasy.mongo.model.Result;
import ru.d1g.doceasy.postgres.model.Module;
import ru.d1g.doceasy.postgres.model.TaskJob;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Route(value = "results", layout = MainView.class)
public class ResultsView extends VerticalLayout {

    public ResultsView(ResultService resultService, ImageService imageService, TaskJobService taskJobService) {
        ConvertedDefaultCrudFormFactory<Result> defaultCrudFormFactory = new ConvertedDefaultCrudFormFactory<>(Result.class, new FormLayout.ResponsiveStep("100%", 1));
        defaultCrudFormFactory.setCancelButtonCaption("Отмена");
        defaultCrudFormFactory.setButtonCaption(CrudOperation.READ, "Отмена");
        defaultCrudFormFactory.setButtonCaption(CrudOperation.ADD, "Добавить");
        defaultCrudFormFactory.setButtonCaption(CrudOperation.UPDATE, "Сохранить");
        defaultCrudFormFactory.setButtonCaption(CrudOperation.DELETE, "Удалить");

        // crud instance
        GridCrudEnchanted<Result> crud = new GridCrudEnchanted<>(Result.class, new VerticalSplitCrudLayout(), defaultCrudFormFactory);

        // grid configuration
        crud.setAddOperationVisible(false);
        crud.setUpdateOperationVisible(false);
        crud.setFindAllOperation(resultService::findAll);
        crud.setDeleteOperation(resultService::remove);

        crud.getGrid().removeAllColumns();
        crud.getGrid().addColumn("id").setWidth("19%");
//        crud.getGrid().addColumn(result -> {
//            return new ComponentRenderer<>(()-> {
//                Label label = new Label("Документ");
//                label.getElement().setProperty("description", "<h1>hello</h1>");
//                return label;
//                return new Icon(VaadinIcon.FEMALE);
//            });
//        });
        crud.getGrid().addColumn(result -> {
            return taskJobService.getByImageIds(Lists.newArrayList(result.getImageId())).stream().findFirst().map(TaskJob::getName).orElse("");
        }).setHeader("Задание");
        crud.getGrid().setColumnReorderingAllowed(true);
        crud.getGrid().addItemDoubleClickListener(event -> crud.updateObject(event.getItem()));

        // form configuration

        crud.getCrudFormFactory().setUseBeanValidation(true);
        crud.getCrudFormFactory().setVisibleProperties("data");
        crud.getCrudFormFactory().setFieldCaptions("Данные");

        crud.getCrudFormFactory().setFieldProvider("data", () -> {
            TextArea textArea = new TextArea();
            return textArea;
        });
        crud.getCrudFormFactory().setConverter("data", new Converter<String, List<Map<String, Object>>>() {
            @Override
            public com.vaadin.flow.data.binder.Result<List<Map<String, Object>>> convertToModel(String value, ValueContext context) {
                return com.vaadin.flow.data.binder.Result.ok(new ArrayList<>());
            }

            @Override
            public String convertToPresentation(List<Map<String, Object>> value, ValueContext context) {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });


        // layout configuration
        setSizeFull();
        add(crud);

    }
}
