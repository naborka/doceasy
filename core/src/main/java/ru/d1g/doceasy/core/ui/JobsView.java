package ru.d1g.doceasy.core.ui;

import com.google.common.collect.Sets;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.Route;
import org.springframework.scheduling.config.Task;
import org.vaadin.crudui.crud.CrudOperation;
import org.vaadin.crudui.form.impl.field.provider.ComboBoxProvider;
import org.vaadin.crudui.form.impl.form.factory.DefaultCrudFormFactory;
import org.vaadin.crudui.layout.impl.VerticalCrudLayout;
import ru.d1g.doceasy.core.service.iface.ModuleService;
import ru.d1g.doceasy.core.service.iface.TaskJobService;
import ru.d1g.doceasy.core.ui.crud.ConvertedDefaultCrudFormFactory;
import ru.d1g.doceasy.core.ui.crud.GridCrudEnchanted;
import ru.d1g.doceasy.postgres.model.Module;
import ru.d1g.doceasy.postgres.model.TaskJob;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

@Route(value = "jobs", layout = MainView.class)
public class JobsView extends VerticalLayout {

    public JobsView(TaskJobService taskJobService, ModuleService moduleService) {
        ConvertedDefaultCrudFormFactory<TaskJob> defaultCrudFormFactory = new ConvertedDefaultCrudFormFactory<>(TaskJob.class, new FormLayout.ResponsiveStep("100em", 1));
        defaultCrudFormFactory.setCancelButtonCaption("Отмена");
        defaultCrudFormFactory.setButtonCaption(CrudOperation.READ, "Отмена");
        defaultCrudFormFactory.setButtonCaption(CrudOperation.ADD, "Добавить");
        defaultCrudFormFactory.setButtonCaption(CrudOperation.UPDATE, "Сохранить");
        defaultCrudFormFactory.setButtonCaption(CrudOperation.DELETE, "Удалить");

        // crud instance
        GridCrudEnchanted<TaskJob> crud = new GridCrudEnchanted<>(TaskJob.class, new VerticalCrudLayout(), defaultCrudFormFactory);

        // grid configuration
        crud.getGrid().removeAllColumns();
        crud.getGrid().addColumn("id").setWidth("19%");
        crud.getGrid().addColumn(taskJob -> {
            if (taskJob.getCreatedDate() != null) {
                return LocalDateTime.ofInstant(taskJob.getCreatedDate(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            } else {
                return "";
            }
        }).setHeader("Создано").setWidth("7%");
        crud.getGrid().addColumn("name").setWidth("25%").setHeader("Задание");
        crud.getGrid().addColumn(taskJob -> {
            if (taskJob.getModule() != null && taskJob.getModule().getName() != null) {
                return taskJob.getModule().getName();
            } else {
                return "";
            }
        }).setHeader("Модуль").setWidth("30%");

        crud.getGrid().addColumn(taskJob -> taskJob.getImageIds().size()).setHeader("Кол-во изображений").setWidth("9%");
        crud.getGrid().setColumnReorderingAllowed(true);
        crud.getGrid().addItemDoubleClickListener(event -> crud.updateObject(event.getItem()));

        // form configuration

        crud.getCrudFormFactory().setUseBeanValidation(true);
        crud.getCrudFormFactory().setVisibleProperties("name", "module", "imageIds");
        crud.getCrudFormFactory().setFieldCaptions("Название", "Модуль", "URLs");

        crud.getCrudFormFactory().setFieldProvider("module", new ComboBoxProvider<>("Модуль", moduleService.findAll(), new TextRenderer<>(Module::getName), Module::getName));
        crud.getCrudFormFactory().setFieldProvider("imageIds", () -> {
            TextArea textArea = new TextArea();
            return textArea;
        });
        crud.getCrudFormFactory().setConverter("imageIds", new Converter<String, Set<String>>() {
            @Override
            public Result<Set<String>> convertToModel(String value, ValueContext valueContext) {
                if (value == null) {
                    return Result.ok(new HashSet<>());
                }
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
                domainObject -> {
                    TaskJob taskJob = taskJobService.save(domainObject);
                    taskJobService.run(taskJob);
                    return taskJob;
                },
                taskJobService::save,
                taskJobService::delete
        );
    }
}
