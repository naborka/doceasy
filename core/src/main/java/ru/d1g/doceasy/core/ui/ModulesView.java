package ru.d1g.doceasy.core.ui;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.crudui.crud.CrudOperation;
import org.vaadin.crudui.crud.impl.GridCrud;
import org.vaadin.crudui.layout.impl.HorizontalSplitCrudLayout;
import ru.d1g.doceasy.core.service.iface.ModuleService;
import ru.d1g.doceasy.postgres.model.Module;

@Route(value = "modules", layout = MainView.class)
public class ModulesView extends VerticalLayout {

    public ModulesView(ModuleService moduleService) {
        // crud instance
        GridCrud<Module> crud = new GridCrud<>(Module.class, new HorizontalSplitCrudLayout());
        crud.setClickRowToUpdate(true);
        crud.setUpdateOperationVisible(false);

        // grid configuration
        crud.getGrid().setColumns("name", "url");
        crud.getGrid().setColumnReorderingAllowed(true);

        // form configuration
        crud.getCrudFormFactory().setUseBeanValidation(true);
        crud.getCrudFormFactory().setVisibleProperties(
                CrudOperation.ADD,
                "name", "url");
        crud.getCrudFormFactory().setVisibleProperties(
                "name", "url");

        // layout configuration
        setSizeFull();
        add(crud);

        // logic configuration
        crud.setOperations(
                moduleService::findAll,
                moduleService::save,
                moduleService::save,
                moduleService::delete
        );
    }

}
