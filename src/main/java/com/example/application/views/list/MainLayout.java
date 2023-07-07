package com.example.application.views.list;

import com.example.application.data.service.SecurityService;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightAction;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.Router;
import com.vaadin.flow.router.RouterLink;
import org.apache.http.Header;


public class MainLayout extends AppLayout {


    private final SecurityService securityService;

    public MainLayout(SecurityService securityService) {
        this.securityService = securityService;
        createHeader();
        createDrawer();
    }

    private void createDrawer() {

        RouterLink list = new RouterLink("List", ListView.class);
        list.setHighlightCondition(HighlightConditions.sameLocation());
        addToDrawer(
                new VerticalLayout(
                        list,
                        new RouterLink("Dashboard", DashboardView.class)
                )
        );

    }

    private void createHeader() {

        H1 logo = new H1("Vaadin CRM");
        logo.addClassNames("text-l", "m-m");

        Button logOutBtn = new Button("Log out");
        logOutBtn.addClassName("logout-btn");
        logOutBtn.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> securityService.logout());

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo, logOutBtn);

        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidthFull();
        header.addClassNames("py-0", "px-m");
        addToNavbar(header);

    }
}
