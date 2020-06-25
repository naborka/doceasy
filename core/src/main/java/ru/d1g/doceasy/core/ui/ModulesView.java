package ru.d1g.doceasy.core.ui;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.crudui.crud.CrudOperation;
import org.vaadin.crudui.form.impl.form.factory.DefaultCrudFormFactory;
import org.vaadin.crudui.layout.impl.VerticalCrudLayout;
import ru.d1g.doceasy.core.service.iface.ModuleService;
import ru.d1g.doceasy.core.ui.crud.GridCrudEnchanted;
import ru.d1g.doceasy.postgres.model.Module;

@Route(value = "modules", layout = MainView.class)
public class ModulesView extends VerticalLayout {

    public ModulesView(ModuleService moduleService) {
        DefaultCrudFormFactory<Module> defaultCrudFormFactory = new DefaultCrudFormFactory<>(Module.class, new FormLayout.ResponsiveStep("100em", 1));
        defaultCrudFormFactory.setCancelButtonCaption("Отмена");
        defaultCrudFormFactory.setButtonCaption(CrudOperation.READ, "Отмена");
        defaultCrudFormFactory.setButtonCaption(CrudOperation.ADD, "Добавить");
        defaultCrudFormFactory.setButtonCaption(CrudOperation.UPDATE, "Сохранить");
        defaultCrudFormFactory.setButtonCaption(CrudOperation.DELETE, "Удалить");

        // crud instance
        GridCrudEnchanted<Module> crud = new GridCrudEnchanted<>(Module.class, new VerticalCrudLayout(), defaultCrudFormFactory);
        crud.setClickRowToUpdate(true);
        crud.setUpdateOperationVisible(false);

        // grid configuration
        crud.getGrid().removeAllColumns();
        crud.getGrid().addColumn("name").setWidth("33%").setHeader("Название");
        crud.getGrid().addColumn("url").setWidth("67%").setHeader("API");
        crud.getGrid().setColumnReorderingAllowed(true);

        // form configuration
        crud.getCrudFormFactory().setUseBeanValidation(true);
        crud.getCrudFormFactory().setVisibleProperties(
                CrudOperation.ADD,
                "name", "url");
        crud.getCrudFormFactory().setVisibleProperties(
                "name", "url");
        crud.getCrudFormFactory().setFieldCaptions("Название", "API URL");
        crud.getCrudFormFactory().setNewInstanceSupplier(() -> {
            Module module = new Module();
            module.setEnabled(true);
            return module;
        });

        // layout configuration
        setSizeFull();
        add(crud);

        // logic configuration
        crud.setOperations(
                moduleService::findAll,
                moduleService::save,
                moduleService::save,
                domainObject -> {
                    domainObject.setEnabled(false);
                    moduleService.save(domainObject);
                }
        );
    }

}
