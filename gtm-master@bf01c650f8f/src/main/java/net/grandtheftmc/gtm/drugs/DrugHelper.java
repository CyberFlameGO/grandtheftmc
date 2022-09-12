package net.grandtheftmc.gtm.drugs;

import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.drugs.internal.service.Helper;
import net.grandtheftmc.gtm.drugs.internal.service.Service;

/**
 * Created by Remco on 25-3-2017.
 */
public class DrugHelper implements Helper {

    @Override
    public String getHelperName() {
        return "Drug Helper";
    }

    @Override
    public Class<? extends Service> getServiceClass() {
        return DrugService.class;
    }

    private DrugService getService() {
        return (DrugService) GTM.getInstance().getDrugManager().getService();
    }

}
