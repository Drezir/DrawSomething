/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dialogs;

/**
 *
 * @author Adam Ostrozlik
 */
public class DialogException extends Exception {
    
    private final boolean showErrorDialog;
    
    public DialogException(String msg, boolean showErrorDialog){
        super(msg);
        this.showErrorDialog = showErrorDialog;
    }

   public void showErrorDialog(){
       if (showErrorDialog){
           ErrorDialog.INSTANCE.show(super.getLocalizedMessage());
       }
   }
    
    
    
}
