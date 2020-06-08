package ru.d1g.doceasy.core.ui;

import com.google.common.collect.Sets;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.router.Route;
import org.vaadin.crudui.crud.CrudOperation;
import org.vaadin.crudui.crud.impl.GridCrud;
import ru.d1g.doceasy.core.service.iface.ModuleService;
import ru.d1g.doceasy.core.service.iface.TaskJobService;
import ru.d1g.doceasy.postgres.model.TaskJob;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Route(value = "jobs", layout = MainView.class)
public class JobsView extends VerticalLayout {

    public JobsView(TaskJobService taskJobService, ModuleService moduleService) {
        // crud instance
        GridCrud<TaskJob> crud = new GridCrud<>(TaskJob.class);

        // grid configuration
        crud.getGrid().setColumns("id");
        crud.getGrid().addColumn(taskJob -> LocalDateTime.ofInstant(taskJob.getCreatedDate(), ZoneId.systemDefault()).format(DateTimeFormatter.BASIC_ISO_DATE));
        crud.getGrid().addColumn("name");
        crud.getGrid().addColumn(taskJob -> taskJob.getModule().getName()).setHeader("Модуль");
        crud.getGrid().addColumn(taskJob -> taskJob.getImageIds().size()).setHeader("Кол-во изображений");
        crud.getGrid().setColumnReorderingAllowed(true);

        // form configuration
        crud.getCrudFormFactory().setUseBeanValidation(true);
        crud.getCrudFormFactory().setVisibleProperties("name", "module", "imageIds");
        crud.getCrudFormFactory().setVisibleProperties(CrudOperation.ADD, "name", "module", "imageIds");
//        crud.getCrudFormFactory().setConverter("imageIds", new Converter<Set<String>, String>() {
//            @Override
//            public Result<String> convertToModel(Set<String> value, ValueContext context) {
//                return Result.ok(String.join("\n", value));
//            }
//
//            @Override
//            public Set<String> convertToPresentation(String value, ValueContext context) {
//                return Sets.newHashSet(value.split(System.getProperty("line.separator")));
//            }
//        });
        crud.getCrudFormFactory().setFieldProvider("imageIds", () -> {
            TextArea textArea = new TextArea();
            return textArea;
        });
        crud.getCrudFormFactory().setConverter("imageIds", new Converter<String, Set<String>>() {
            @Override
            public Result<Set<String>> convertToModel(String value, ValueContext valueContext) {
                return Result.ok(Sets.newHashSet(value.split(System.getProperty("line.separator"))));
            }

            @Override
            public String convertToPresentation(Set<String> value, ValueContext valueContext) {
                return String.join("\n", value);
            }
        });

        // layout configuration
        setSizeFull();
        add(crud);

        // logic configuration
        crud.setOperations(
                taskJobService::findAll,
                taskJobService::save,
                taskJobService::save,
                taskJobService::delete
        );
    }
}
