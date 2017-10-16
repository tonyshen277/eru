package org.assemblits.eru.gui.model;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.assemblits.eru.comm.actors.Director;
import org.assemblits.eru.comm.modbus.ModbusDeviceReader;
import org.assemblits.eru.entities.Connection;
import org.assemblits.eru.entities.Device;
import org.assemblits.eru.entities.Display;
import org.assemblits.eru.entities.Tag;
import org.assemblits.eru.gui.ApplicationContextHolder;
import org.assemblits.eru.gui.dynamo.DynamoExtractor;
import org.assemblits.eru.gui.dynamo.base.Dynamo;
import org.assemblits.eru.jfx.links.GenericLinker;
import org.assemblits.eru.jfx.links.InvalidObservableLinker;
import org.assemblits.eru.jfx.links.Linker;
import org.assemblits.eru.jfx.listeners.TagAddressInvalidationListener;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Created by mtrujillo on 9/9/2015.
 */
@Slf4j
@Component
@Data
public class ProjectListener {

    private ProjectModel projectModel;

    public void listen(){
        listenDevicesChanges();
        listenConnectionsChanges();
        listenTagsChanges();
        listenUsersChanges();
        listenDisplaysChanges();
    }

    private void listenDevicesChanges(){
        // TODO
    }

    private void listenConnectionsChanges(){
        // Current Connections
        this.projectModel.getConnections().forEach(this::installLink);
        // Future Connections
        this.projectModel.getConnections().addListener((ListChangeListener<Connection>) c -> {
            while (c.next()) {
                for (Connection newConnection : c.getAddedSubList()) {
                    installLink(newConnection);
                }
            }
        });
    }

    private void listenTagsChanges(){
        // TODO
    }

    private void listenUsersChanges(){
        // TODO
    }

    private void listenDisplaysChanges(){
        // Current Displays
        this.projectModel.getDisplays().forEach(this::installLink);
        // Future Displays
        this.projectModel.getDisplays().addListener((ListChangeListener<Display>) c -> {
            while (c.next()) {
                for (Display newDisplay : c.getAddedSubList()) {
                    installLink(newDisplay);
                }
            }
        });
    }

    private void installLink(Display display){
        display.fxNodeProperty().addListener((o1, oldNode, newNode) -> {
            if(newNode != null){
                DynamoExtractor extractor = new DynamoExtractor();
                List<Dynamo> dynamos = extractor.extractFrom(newNode);
                projectModel.getTags().forEach(tag ->
                        dynamos.stream()
                                .filter(dynamo  -> dynamo.getCurrentValueTagID() == tag.getId())
                                .forEach(dynamo -> tag.valueProperty().addListener((obj, old, newValue) -> dynamo.setCurrentTagValue(newValue)))
                );
            }
        });
    }

    private void installLink(Connection connection){
        ProjectLinks projectLinks = ApplicationContextHolder.getApplicationContext().getBean(ProjectLinks.class);
        connection.connectedProperty().addListener((o, wasConnected, isConnected) -> {
            if (isConnected){
                projectModel.getDevices()
                        .stream()
                        .filter(Device::getEnabled)
                        .filter(device -> device.getConnection().equals(connection))
                        .forEach(device -> {
                            Director director = ApplicationContextHolder.getApplicationContext().getBean(Director.class);
                            ModbusDeviceReader reader = new ModbusDeviceReader(device);
                            BiConsumer<Director, ModbusDeviceReader> startReading = (d, r) -> d.getCommunicators().add(r);
                            BiConsumer<Director, ModbusDeviceReader> stopReading = (d, r) -> {r.stop(); d.getCommunicators().remove(r);};
                            GenericLinker<Director, ModbusDeviceReader> linker = new GenericLinker<>(director, reader, startReading, stopReading);
                            projectLinks.getConnectionLinksContainer().addLink(connection, linker);
                        });
                projectModel.getTags()
                        .stream()
                        .filter(Tag::getEnabled)
                        .filter(tag -> tag.getLinkedAddress() != null)
                        .filter(tag -> tag.getLinkedAddress().getOwner().getConnection().equals(connection))
                        .forEach(tag -> {
                            ObjectProperty<Timestamp> observable = tag.getLinkedAddress().timestampProperty();
                            TagAddressInvalidationListener listener = new TagAddressInvalidationListener(tag);
                            InvalidObservableLinker linker = new InvalidObservableLinker(observable, listener);
                            projectLinks.getConnectionLinksContainer().addLink(connection, linker);
                        });
                projectLinks.getConnectionLinksContainer().getLinksOf(connection).forEach(Linker::link);
            } else {
                projectLinks.getConnectionLinksContainer().getLinksOf(connection).forEach(Linker::unlink);
                projectLinks.getConnectionLinksContainer().removeAllLinks(connection);
            }
        });
    }

    public void setProjectModelAndListen(ProjectModel projectModel) {
        setProjectModel(projectModel);
        listen();
    }
}


