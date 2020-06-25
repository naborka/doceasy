package ru.d1g.doceasy.core.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PageConfigurator;

import java.util.HashMap;
import java.util.Map;

@Route
public class MainView extends AppLayout implements BeforeEnterObserver, AfterNavigationObserver, PageConfigurator {

    private Tabs tabs = new Tabs();
    private Map<Tab, Class<? extends HasComponents>> tabToView = new HashMap<>();
    private Map<Class<? extends HasComponents>, Tab> viewToTab = new HashMap<>();

    public MainView() {
        AppLayout appLayout = new AppLayout();

        Icon icon = new Icon(VaadinIcon.FILE_TEXT);
        icon.setSize("55");
        Label doceasy = new Label("DOCEASY");
        addToNavbar(icon, doceasy);

        tabs.addSelectedChangeListener(event -> tabsSelectionChanged(event));
        addToNavbar(tabs);

        addTab(ModulesView.class, "Модули");
        addTab(JobsView.class, "Задания");
        addTab(ResultsView.class, "Результаты");
    }

    private void addTab(Class<? extends HasComponents> clazz, String name) {
        Tab tab = new Tab(name);
        tabs.add(tab);
        tabToView.put(tab, clazz);
        viewToTab.put(clazz, tab);
    }

    private void tabsSelectionChanged(Tabs.SelectedChangeEvent event) {
        if (event.isFromClient()) {
            UI.getCurrent().navigate((Class<? extends Component>) tabToView.get(event.getSelectedTab()));
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        selectTabByCurrentView(event);
    }

    public void selectTabByCurrentView(BeforeEnterEvent event) {
        Class<?> viewClass = event.getNavigationTarget();
        tabs.setSelectedTab(viewToTab.get(viewClass));
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        updatePageTitle();
    }

    public void updatePageTitle() {
        Class<? extends HasComponents> viewClass = tabToView.get(tabs.getSelectedTab());
        if (viewClass != null) {
            UI.getCurrent().getPage().setTitle(DemoUtils.getViewName(viewClass) + " - " + "Crud UI add-on demo");
        }
    }

    @Override
    public void configurePage(InitialPageSettings settings) {
        updatePageTitle();
    }

}
