package PrintApp.V3.Clases;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.IdRes;

public class BaseCompatAcivity extends AppCompatActivity {
    public static final String EXTRA_ITEMS          = "com.mobile.proisa.pedidoprueba.Activities.EXTRA_ITEMS";
    public static final String EXTRA_CLIENT         = "com.mobile.proisa.pedidoprueba.Activities.EXTRA_CLIENT";
    public static final String EXTRA_INIT_VISIT     = "com.mobile.proisa.pedidoprueba.Activities.EXTRA_INIT_VISIT";
    public static final String EXTRA_ITEM_DATA      = "com.mobile.proisa.pedidoprueba.Activities.EXTRA_ITEM_DATA";
    public static final String EXTRA_INVOICE        = "com.mobile.proisa.pedidoprueba.Activities.EXTRA_INVOICE";
    public static final String EXTRA_USER           = "com.mobile.proisa.pedidoprueba.Activities.EXTRA_USER";
    public static final String EXTRA_IS_NEW_INVOICE = "com.mobile.proisa.pedidoprueba.Activities.EXTRA_IS_NEW_INVOICE";


    public static final int REQUEST_CODE_CAMERA  = 1000;
    public static final int REQUEST_CODE_STORAGE = 1001;
    public static final int REQUEST_CODE_GALLERY = 1002;
    public static final int REQUEST_CODE_INVOICE_DETAILS = 1003;
    //public static final int REQUEST_CODE_

    @Override
    protected void onResume() {
        super.onResume();

        onBindUI();
    }

    /**
     * Puedes usar este método para enlazar los componentes de tú interfaz gráfica.
     * Este método es llamando en el {@link AppCompatActivity#onResume()} de la actividad.
     */
    protected void onBindUI() {
    }


    /**
     * Establece un fragment en un layout que esté cargado {@link AppCompatActivity#setContentView}
     * en esta actividad
     * @param containerId id del layout donde será cargado el fragment
     * @param fragment fragmento a ser cargado
     * @param statusLosses confirmar si se usar {@link FragmentTransaction#commitAllowingStateLoss()}
     * @return
     */
    private int setCurrentFragment(@IdRes int containerId, Fragment fragment, boolean statusLosses){

        if(fragment == null){
            return -1;
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(containerId, fragment);

        if(statusLosses)
            return fragmentTransaction.commitAllowingStateLoss();
        else
            return fragmentTransaction.commit();
    }

    public int setCurrentFragment(@IdRes int containerId, Fragment fragment){
        return setCurrentFragment(containerId, fragment, false);
    }

    public int setCurrentFragmentWithStateLoss(@IdRes int containerId, Fragment fragment){
        return setCurrentFragment(containerId, fragment, true);
    }

  
}
