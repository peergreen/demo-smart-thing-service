package com.peergreen.demo.weconsole;

import javax.servlet.annotation.WebServlet;

import com.peergreen.demo.smartthing.json.ChannelInfo;
import com.peergreen.demo.smartthing.json.DeviceInfo;
import com.peergreen.demo.smartthing.json.SensorInfo;
import com.peergreen.demo.weconsole.node.ChannelNode;
import com.peergreen.demo.weconsole.node.DeviceNode;
import com.peergreen.demo.weconsole.node.SensorNode;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Container;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("mytheme")
@SuppressWarnings("serial")
@Push
public class MyVaadinUI extends UI  {

    public static final String ENTRY_NAME = "name";
    public static final String ICON = "icon";

    private Container container;

    private Tree tree;

    private Label label;
    private Label labelValue;


    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = MyVaadinUI.class, widgetset = "com.peergreen.demo.weconsole.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
    }



    @Override
    protected void init(VaadinRequest request) {

        final HorizontalLayout layout = new HorizontalLayout();
        layout.setMargin(true);
        setContent(layout);

        VerticalLayout leftContent = new VerticalLayout();
        layout.addComponent(leftContent);
        VerticalLayout rightContent = new VerticalLayout();
        layout.addComponent(rightContent);


        this.tree = new Tree();
        tree.setImmediate(true);

        leftContent.addComponent(tree);

        Panel panel = new Panel("Details");
        panel.setWidth("300px");

        this.label = new Label();
        label.setContentMode(ContentMode.HTML);
        rightContent.addComponent(label);


        this.container = new HierarchicalContainer();
        // Make sure you have properties defined in your container
        container.addContainerProperty(ENTRY_NAME, String.class, null);
        tree.setContainerDataSource(container);

        container.addContainerProperty(ICON, Resource.class, null);
        tree.setItemIconPropertyId(ICON);

        // Define which property is going to be used as the item's caption in the tree
        tree.setItemCaptionPropertyId(ENTRY_NAME);
        tree.setWidth("50em");


        this.labelValue = new Label();
        rightContent.addComponent(labelValue);


        new InitializerThread().start();
        new ChannelValueThread().start();

        tree.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(final ValueChangeEvent event) {

                Object value = event.getProperty().getValue();
                if (value instanceof DeviceNode) {
                    DeviceNode device = (DeviceNode) value;
                    // click on a device
                    label.setValue(getDeviceInfo(device.getDeviceInfo()));
                    labelValue.setValue("");
                    labelValue.setVisible(false);
                } else if (value instanceof SensorNode) {
                 // click on a sensor
                    SensorNode sensor = (SensorNode) value;
                    // click on a device
                    label.setValue(getSensorInfo(sensor.getSensorInfo()));
                    labelValue.setValue("");
                    labelValue.setVisible(false);
                } else if (value instanceof ChannelNode) {
                    // click on a channel
                    ChannelNode channel = (ChannelNode) value;
                    // click on a channel
                    label.setValue(getChannelInfo(channel.getChannelInfo()));
                    labelValue.setValue("Getting value...");
                    labelValue.setVisible(true);
                }

            }
        });

    }




    public String getDeviceInfo(DeviceInfo deviceInfo) {
        StringBuilder sb = new StringBuilder("<b>Manufacturer</b>: ");
        sb.append(deviceInfo.getManufacturer());
        sb.append("<br>");
        sb.append("<b>Model</b>: ");
        sb.append(deviceInfo.getModel());
        sb.append("<br>");
        sb.append("<b>Operatin System</b>: ");
        sb.append(deviceInfo.getOs());
        sb.append("<br>");
        sb.append("<b>Owner</b>: ");
        sb.append(deviceInfo.getOwner());
        sb.append("<br>");
        return sb.toString();
    }


    public String getSensorInfo(SensorInfo sensorInfo) {
        StringBuilder sb = new StringBuilder("<b>Name</b>: ");
        sb.append(sensorInfo.getName());
        sb.append("<br>");
        sb.append("<b>Location</b>: ");
        sb.append(sensorInfo.getLocation());
        return sb.toString();
    }

    public String getChannelInfo(ChannelInfo channelInfo) {
        StringBuilder sb = new StringBuilder("<b>ID</b>: ");
        sb.append(channelInfo.getId());
        sb.append("<br>");
        sb.append("<b>Unit</b>: ");
        String unit = channelInfo.getUnit();
        if (unit == null || unit.equals("")) {
            sb.append("N/A");
        } else {
            sb.append(unit);
        }
        return sb.toString();
    }




    class InitializerThread extends Thread {
        @Override
        public void run() {

            RefreshTask timerTask = new RefreshTask(container, tree);

            while (true) {
                timerTask.updateAllTree();
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                }

            }
        }
    }



    class ChannelValueThread extends Thread {
        @Override
        public void run() {

            RefreshSubTask timerTask = new RefreshSubTask(container, tree, labelValue);

            while (true) {
                timerTask.updateOnlyCell();
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                }

            }
        }
    }



}
