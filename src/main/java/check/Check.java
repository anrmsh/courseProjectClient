package check;
import controllers.RegistrationController;
import check.Dialog;
public class Check {
    private RegistrationController controller;

    public Check(RegistrationController controller) {
        this.controller = controller;
    }

    public  boolean validateInput() {
        if (controller.getLoginField().getText().isEmpty() || controller.getPasswordField().getText().isEmpty()|| controller.getRptPasswordField().getText().isEmpty() ||
                controller.getNameField().getText().isEmpty() || controller.getLastNameField().getText().isEmpty() ||
                controller.getPositionSelector().getSelectionModel() == null) {
            Dialog.showAlert("Ошибка", "Все поля должны быть заполнены!");
            return false;
        }
if(controller.getMailField().getText()!=null){
    if (!controller.getMailField().getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
        Dialog.showAlert("Ошибка", "Некорректный формат email!");
        return false;
    }
}


        if (controller.getPasswordField().getText().length() < 6) {
            Dialog.showAlert("Ошибка", "Пароль должен содержать минимум 6 символов!");
            return false;
        }

        return true; // Все проверки пройдены
    }
}
