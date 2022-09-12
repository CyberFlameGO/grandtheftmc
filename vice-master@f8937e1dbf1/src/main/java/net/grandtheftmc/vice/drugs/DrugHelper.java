package net.grandtheftmc.vice.drugs;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.drugs.internal.service.Helper;
import net.grandtheftmc.vice.drugs.internal.service.Service;

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
        return (DrugService) Vice.getInstance().getDrugManager().getService();
    }

}
