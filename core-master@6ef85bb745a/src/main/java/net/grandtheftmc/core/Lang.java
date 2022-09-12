package net.grandtheftmc.core;

import net.grandtheftmc.core.util.C;
import net.grandtheftmc.core.util.Utils;

public enum Lang {

    NOPERM("&cYou don't have permission to execute this command!"),
    CHRISTMAS("&a&lC&c&lH&a&lR&c&lI&a&lS&c&lT&a&lM&c&lA&a&lS&8&l> "),
    NOTPLAYER("&cYou are not a player!"),
    MSG(" &a&lMSG&8&l> "),
    TRADE(" &2&lTRADE&8&l> "),
    BUCKS(" &a&lBUCKS&8&l> "),
    BUCKS_ADD(" &a&lBUCKS&8&l> &a&l+&a $&l"),
    BUCKS_TAKE(" &a&lBUCKS&8&l> &c&l-&c $&l"),
    TOKENS(" &e&lTOKENS&8&l> "),
    TOKENS_ADD(" &e&lTOKENS&8&l> &e&l+ "),
    TOKENS_TAKE(" &e&lTOKENS&8&l> &c&l- "),
    MONEY(" &3&lMONEY&8&l> "),
    MONEY_ADD(" &3&lMONEY&8&l> &a&l+&a $&l"),
    MONEY_TAKE(" &3&lMONEY&8&l> &c&l-&c $&l"),
    CROWBARS(" &9&lCROWBARS&8&l> "),
    CROWBARS_ADD(" &9&lCROWBARS&8&l> &9&l+ "),
    CROWBARS_TAKE(" &9&lCROWBARS&8&l> &c&l- "),
    ATM(" &3&lATM&8&l> "),
    BANK(" &3&lBANK&8&l> "),
    BANK_ADD("&3&lBANK&8&l> &a&l+&a $&l"),
    BANK_TAKE("&3&lBANK&8&l> &c&l-&c $&l"),
    AMMO(" &c&lAMMO&8&l> "),
    AMMO_ADD(" &c&lAMMO&8&l> &a&l+ "),
    AMMO_TAKE(" &c&lAMMO&8&l> &c&l- "),
    GTM(" &7&l" + Core.getSettings().getServer_GTM_shortName() + "&8&l> "),
    CHEAT_CODES(" &2&lCHEATCODE&8&l> "),
    PREFS(" &5&lPREFS&8&l> "),
    AMMUNATION(" &9&lAMMU&4&lNATION&8&l> "),
    WANTED(" &c&lWANTED&8&l> "),
    TAXI(" &e&lTAXI&8&l> "),
    WARP(" &e&lWARP&8&l> "),
    SHOP(" &a&lSHOP&8&l> "),
    TRASH_CAN(" &7&lTRASH CAN&8&l> "),
    HEY(" &c&lHEY&8&l> "),
    COMBATTAG(" &7&lCOMBATTAG&8&l> "),
    GUARDPETS(" &c&lGUARD PETS&8&l> "),
    BOUNTIES(" &5&lBOUNTIES&8&l> "),
    VOTE(" &e&lVOTE&8&l> "),
    TOKEN_SHOP(" &e&lTOKEN SHOP&8&l> "),
    CRATES(" &6&lCRATES&8&l> "),
    KITS(" &b&lKITS&8&l> "),
    GAMEITEMS(" &a&lGAMEITEMS&8&l> "),
    RANKUP(" &a&lRANKUP&8&l> "),
    RANKS(" &a&lRANKS&8&l> "),
    JOBS(" &3&lJOBS&8&l> "),
    COP_MODE(" &3&lCOP MODE&8&l> "),
    COPS(" &3&lCOPS&8&l> "),
    COP(" &3&lCOP&8&l> "),
    POLICE(" &3&lPOLICE&8&l> "),
    BRIBE(" &3&lBRIBE&8&l> "),
    HITMAN_MODE(" &8&lHITMAN MODE> "),
    JAIL(" &c&lJAIL&8&l> "),
    GPS(" &7&lGPS&8&l> "),
    HOUSES(" &3&lHOUSES&8&l> "),
    TUTORIALS(" &2&lTUTORIALS&8&l> "),
    GANGS(" &a&lGANGS&8&l> "),
    GANGCHAT(" &a&lGANGCHAT&8&l> "),
    LOOTCRATES(" &e&lLOOTCRATES&8&l> "),
    GLIDERS(" &c&lGLIDERS&8&l> "),
    HUB("&6&lHUB&8&l> "),
    SOCIALSPY(" &c&lSOCIALSPY&8&l> "),
    VANISH(" &c&lVANISH&8&l> "),
    SS(" &c&lSS&8&l> "),
    SERVERS(" &6&lSERVERS&8&l> "),
    COSMETICS(" &6&lCOSMETICS&8&l> "),
    NAMETAGS(" &e&lNAMETAGS&8&l> "),
    REWARDS(" &a&lREWARDS&8&l> "),
    VEHICLES(" &4&lVEHICLES&8&l> "),
    DEATH(" &c&lDEATH&8&l> "),
    HEAD_AUCTION(" &e&lHEAD AUCTION&8&l> "),
    ARMOR_UPGRADE(" &b&lARMOR UPGRADE&8&l> "),
    ANTIAURA(" &c&lANTIAURA&8&l> "),
    SAVE(" &c&lSAVE&8&l> "),
    ACHIEVEMENT(" &c&lACHIEVEMENTS&8&l> "),
    GTW(" &e&lGTW&8&l> "),
    DRUGS(" &e&lDRUGS&8&L> "),
    LOTTERY(" &e&lLOTTERY&8&l> "),
    ANTISPAM(" &c&lANTISPAM&8&l> "),
    ANTIAD(" &c&lANTIADVERT&8&l> "),
    VICE(" &d&lVICE&8&l> "),
    DISCORD(" &5&lDISCORD&8&l> "),
    FACEBOOK(" &1&lFACEBOOK&8&l> "),
    TWITTER(" &3&lTWITTER&8&l> "),
    SPANK(" &d&lS&a&lP&e&lA&9&lN&b&lK&8&l>&7 "),
    QUEUE(" &e&lQUEUE&8&l> "),
    ALERTS(" &c&lALERTS&8&l>&7 "),
    CASINO(" &9&lCASINO&8&l>&7 "),
    ANTICHEAT(" " + C.RED + C.BOLD + "WATCHDAWG" + C.DARK_GRAY + C.BOLD + ">" + C.RESET + " "),
    ;

    private final String s;

    Lang(String s) {
        this.s = s;
    }

    public String s() {
        return Utils.f(this.s);
    }

    @Override
    public String toString() {
        return Utils.f(this.s);
    }

    public String f(String s) {
        return Utils.f(this.s + s);
    }
}
