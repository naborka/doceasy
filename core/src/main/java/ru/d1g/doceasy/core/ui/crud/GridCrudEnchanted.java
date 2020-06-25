package ru.d1g.doceasy.core.ui.crud;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import org.vaadin.crudui.crud.CrudListener;
import org.vaadin.crudui.crud.CrudOperation;
import org.vaadin.crudui.crud.CrudOperationException;
import org.vaadin.crudui.crud.impl.GridCrud;
import org.vaadin.crudui.form.CrudFormFactory;
import org.vaadin.crudui.layout.CrudLayout;

public class GridCrudEnchanted<T> extends GridCrud<T> {
    public GridCrudEnchanted(Class<T> domainType) {
        super(domainType);
    }

    public GridCrudEnchanted(Class<T> domainType, CrudLayout crudLayout) {
        super(domainType, crudLayout);
    }

    public GridCrudEnchanted(Class<T> domainType, CrudFormFactory<T> crudFormFactory) {
        super(domainType, crudFormFactory);
    }

    public GridCrudEnchanted(Class<T> domainType, CrudListener<T> crudListener) {
        super(domainType, crudListener);
    }

    public GridCrudEnchanted(Class<T> domainType, CrudLayout crudLayout, CrudFormFactory<T> crudFormFactory) {
        super(domainType, crudLayout, crudFormFactory);
    }

    public GridCrudEnchanted(Class<T> domainType, CrudLayout crudLayout, CrudFormFactory<T> crudFormFactory, CrudListener<T> crudListener) {
        super(domainType, crudLayout, crudFormFactory, crudListener);
    }

    @Override
    protected void initLayout() {
        setRowCountCaption("%d найдено");
        setSavedMessage("Запись сохранена");
        setDeletedMessage("Запись удалена");

        findAllButton = new Button(VaadinIcon.REFRESH.create(), e -> findAllButtonClicked());
        findAllButton.getElement().setAttribute("title", "Обновить список");

        crudLayout.addToolbarComponent(findAllButton);

        addButton = new Button(VaadinIcon.PLUS.create(), e -> addButtonClicked());
        addButton.getElement().setAttribute("title", "Добавить");
        crudLayout.addToolbarComponent(addButton);

        updateButton = new Button(VaadinIcon.PENCIL.create(), e -> updateButtonClicked());
        updateButton.getElement().setAttribute("title", "Сохранить");
        crudLayout.addToolbarComponent(updateButton);

        deleteButton = new Button(VaadinIcon.TRASH.create(), e -> deleteButtonClicked());
        deleteButton.getElement().setAttribute("title", "Удалить");
        crudLayout.addToolbarComponent(deleteButton);

        grid = new Grid<>(domainType);
        grid.addSelectionListener(e -> gridSelectionChanged());
        crudLayout.setMainComponent(grid);

        updateButtons();
    }

    public void updateObject(T object) {
        showForm(CrudOperation.UPDATE, object, false, savedMessage, event -> {
            try {
                updateOperation.perform(object);
                refreshGrid();
                // TODO: grid.scrollTo(updatedObject);
            } catch (IllegalArgumentException ignore) {
            } catch (CrudOperationException e1) {
                refreshGrid();
            } catch (Exception e2) {
                refreshGrid();
                throw e2;
            }
        });
    }
}
