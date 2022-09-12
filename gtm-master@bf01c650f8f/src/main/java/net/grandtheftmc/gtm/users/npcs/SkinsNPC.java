package net.grandtheftmc.gtm.users.npcs;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import net.citizensnpcs.api.event.NPCCollisionEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCPushEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.grandtheftmc.core.npc.CoreNPC;
import net.grandtheftmc.core.npc.interfaces.ClickableNPC;
import net.grandtheftmc.core.npc.interfaces.CollideableNPC;
import net.grandtheftmc.gtm.weapon.skins.menu.MainMenu;

public class SkinsNPC extends CoreNPC implements ClickableNPC, CollideableNPC {
    public SkinsNPC(Location location) {
        super(location, EntityType.PLAYER, "&9&lMr Skinner", "&7&oManage and view your weapon skins here!");
    }

    @Override
    protected void generateNewNPC() {
        setLookClose(true);
        setSkin("eyJ0aW1lc3RhbXAiOjE1MTg1NjMxNzk3MDEsInByb2ZpbGVJZCI6Ijc2MTJhZWU1YTk5YjQxZWRiYTg3Nzg4MGMyMjZiMzM2IiwicHJvZmlsZU5hbWUiOiJGbG91cmVrIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yNjE0ODNmMGIxNjc2OWQ2NGFlMTI5Yjg5ZDYyY2M3N2M1Mzk0OGRlZTVmYzQ2MmFlNTQ1OTQyZWYwNDdjNzliIn19fQ==",
                "svzvIYm7x02JAMpjnW+oKQAM48jpzTNvKtLVSPGuL7DjWjZOuH+fjrw6tqGWKAHZYbR7QaIfNiHKk/uKbF8gGij9jxphAZuD47p53ALODYwbopir2lSdmMl+flPggTS7dWYmPfQsnSa4t13O1yLZMmdtFUTzyJQYZqFeU+Ss7CAiU0Xxvi5SNqlrLlm+utDaQQoobfkHeuHBe1/bFDNrTx7iYGYuPqn3Y5T8YtqgyRVaQsoDyeAmONNJE5R/o8MVOEFKtqE/MqL5ONpAFkkM1iDlajj+C3lhJ/3ORnpA6HTTyAGvLLZJNXBNM2+nl4x7hyEMw6kQJ5JCh3uFGbJamXvpi/pFQS2rBI1OZVzhZbYIE9/dfqxK6MH7A9Z1XlCPYQPFmQzh0unJH8y4OTBxfUsEjaIgB8AG61SjuyJqFo5oeJ2DJ1kO8ua72tLl13ron/fk+AfGeW16LuwxIy1XaUki4UC5MRcE0wj7ou68ME1gR/XmjpQDuaPaJj7yf+epFHc9lDARtb21yqOkYJXAKiN6ec8HU7uhIXW142K+bhzFGTfUZ6jxnL54TQ6qWojwpM/VEM8ewsMaL4Xf3Kh5hPzuXeylca4qPZzhsJ2I9Bf09i1TknDVTnW8qgmz4WZynyBNMdWZ60w25q/afM1DB9QuzUo2Qa+y2z7MSDALMCw=");
        setCollideable(true);
    }

    @Override
    public void onRightClick(NPCRightClickEvent event) {
        new MainMenu(event.getClicker()).open();
    }

    @Override
    public void onLeftClick(NPCLeftClickEvent npcLeftClickEvent) {

    }

    @Override
    public void onCollide(NPCCollisionEvent event) {
        
    }

    @Override
    public void onPush(NPCPushEvent event) {
        
    }
}
