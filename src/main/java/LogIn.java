import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.event.ActionEvent;
import org.json.JSONArray;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;


public class LogIn extends Application implements Initializable{

    @FXML
    private AnchorPane parentPane, paneError;

    @FXML
    private PasswordField txtPass;

    @FXML
    private ComboBox<String> comboboxUsuario;

    @FXML
    Label labelError;

    ObservableList<String> listaLugares = FXCollections.observableArrayList();

    @FXML
    public void contrasenaOlvidadaActionPerformed(MouseEvent event) throws IOException {
        helper.show("restorePass.fxml", parentPane);
    }


    @FXML
    public void btnAceptarActionPerformed(ActionEvent event) throws IOException {
        JSONArray jsonArray = null;
        try{
            if(comboboxUsuario.getValue().length() > 0 && txtPass.getText().length() > 0){
                jsonArray = rest.GET(
                        routes.getRoute(
                                Routes.routesName.GET_PASS,
                                comboboxUsuario.getValue(),
                                helper.hash(txtPass.getText())
                        )
                );
            }
            else{
                labelError.setText("Usuario o contraseña incorrectos");
                paneError.setVisible(true);
            }
            if(jsonArray != null){

                if(jsonArray.getJSONObject(0).get("acceso").toString().contains("true")){
                    UsuarioEntity.getUsuario(comboboxUsuario.getValue());
                    if(helper.hash(txtPass.getText()).equals(helper.hash(helper.defaultPass))){
                        helper.show("cambioPass.fxml", parentPane);
                    }
                    else{
                        if(comboboxUsuario.getValue().equals("Administrador")){
                            helper.show("usuarioAdmin.fxml", parentPane);
                        }
                        else{
                            helper.show("usuarioNormal.fxml", parentPane);
                        }
                    }
                }
                else{
                    labelError.setText("Usuario o contraseña incorrectos");
                    paneError.setVisible(true);
                }

            }
            else{
                labelError.setText("Ocurrió un error inesperado");
                paneError.setVisible(true);
            }
        }
        catch(RuntimeException re){
            labelError.setText("Usuario o contraseña incorrectos");
            paneError.setVisible(true);
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        JSONArray jsonArray = null;
        try{
            jsonArray = rest.GET(routes.getRoute(Routes.routesName.GET_USUARIOS));
            if(jsonArray != null){
                for(int i = 0; i < jsonArray.length(); i++){
                    listaLugares.add((String) jsonArray.getJSONObject(i).get("nombre"));
                }
            }
        }
        catch(IOException ioe){
            helper.showAlert("Ocurrió un error inesperado", Alert.AlertType.ERROR);
        }
        listaLugares.sort((Object c1, Object c2)->{
            return c1.toString().compareTo((String) c2);
        });
        this.comboboxUsuario.setItems(listaLugares);
    }

    public void closePopupError(MouseEvent event) {
        paneError.setVisible(false);
    }

}
