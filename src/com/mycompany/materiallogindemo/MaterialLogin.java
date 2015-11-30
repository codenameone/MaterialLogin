package com.mycompany.materiallogindemo;


import com.codename1.components.FloatingHint;
import com.codename1.components.InfiniteProgress;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.Label;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;
import com.codename1.io.Log;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Dialog;
import com.codename1.ui.FontImage;
import com.codename1.ui.TextField;
import com.codename1.ui.Toolbar;
import com.codename1.ui.animations.CommonTransitions;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.util.UITimer;
import com.codename1.ui.validation.LengthConstraint;
import com.codename1.ui.validation.RegexConstraint;
import com.codename1.ui.validation.Validator;
import java.io.IOException;
import java.util.HashMap;

public class MaterialLogin {
    private HashMap<String, String> accounts = new HashMap<>();
    private Form current;
    private Resources theme;

    public void init(Object context) {
        theme = UIManager.initFirstTheme("/theme");
    }
    
    public void start() {
        if(current != null){
            current.show();
            return;
        }
        createForm(false).show();
    }

    public Form createForm(boolean signup) {
        TextField emailTextField = new TextField("", "E-Mail",  20, TextField.EMAILADDR);
        TextField focus = emailTextField;
        TextField passwordTextField = new TextField("", "Password",  20, TextField.PASSWORD);
        Button loginButton;
        Form frm = new Form(new BoxLayout(BoxLayout.Y_AXIS));
        frm.add(theme.getImage("logo.png"));
        
        Button noAccountButton;
        if(signup) {
            TextField name = new TextField("", "Name", 20, TextField.ANY);
            focus = name;
            frm.add(new FloatingHint(name));
            Validator val = new Validator();
            val.setShowErrorMessageForFocusedComponent(true);
            val.setErrorMessageUIID("ErrorMessage");
            val.setValidationFailureHighlightMode(Validator.HighlightMode.EMBLEM);
            val.setValidationFailedEmblem(FontImage.createMaterial(FontImage.MATERIAL_ERROR, name.getSelectedStyle()));
            val.setValidationEmblemPositionX(0.9f);
            val.setValidationEmblemPositionY(0.5f);
            loginButton = new Button("CREATE ACCOUNT");
            val.addSubmitButtons(loginButton);
            val.addConstraint(name, new LengthConstraint(3));
            val.addConstraint(emailTextField, RegexConstraint.validEmail());
            val.addConstraint(passwordTextField, new LengthConstraint(3));
            noAccountButton = new Button("Already a member? Login");
            noAccountButton.addActionListener((e) -> createForm(false).showBack());
            loginButton.addActionListener((e) -> {
                accounts.put(emailTextField.getText(), passwordTextField.getText());
                createForm(false).showBack();
            });
        } else {
            loginButton = new Button("LOGIN");
            noAccountButton = new Button("No account yet? Create one");   
            noAccountButton.addActionListener((e) -> {
                createForm(true).show();
            });
            loginButton.addActionListener((e) -> {
                Dialog dlg = new Dialog();
                dlg.setDialogUIID("RedDialog");
                dlg.setLayout(new BoxLayout(BoxLayout.X_AXIS));
                dlg.add(new InfiniteProgress()).
                    add("Authenticating...");
                dlg.showModeless();
                UITimer uit = new UITimer(() -> {
                   Form success = new Form("Success");
                   success.show();
                });
                uit.schedule(3000, false,dlg);
            });
        }
        noAccountButton.setUIID("CenterGray");
        frm.setEditOnShow(focus);
        
        frm.add(new FloatingHint(emailTextField)).
                add(new FloatingHint(passwordTextField)).
                add(loginButton).
                add(noAccountButton);

        return frm;
    }
    
    public void stop() {
        current = Display.getInstance().getCurrent();
    }
    
    public void destroy() {
    }

}
