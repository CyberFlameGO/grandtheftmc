package net.grandtheftmc.Bungee;

import net.md_5.bungee.api.chat.TextComponent;

public enum Lang {

    GMSG(" &a&lGMSG&8&l> "),
    MSG(" &a&lMSG&8&l> "),
    BUCKS(" &a&lBUCKS&8&l> "),
    BUCKS_ADD(" &a&lBUCKS&8&l> &a&l+&a $&l"),
    BUCKS_TAKE(" &a&lBUCKS&8&l> &c&l-&c $&l"),
    TOKENS(" &e&lTOKENS&8&l> "),
    TOKENS_ADD(" &e&lTOKENS&8&l> &e&l+&e&l"),
    TOKENS_TAKE(" &e&lTOKENS&8&l> &c&l-&c&l"),
    MONEY(" &3&lMONEY&8&l> "),
    MONEY_ADD(" &3&lMONEY&8&l> &a&l+&a $&l"),
    MONEY_TAKE(" &3&lMONEY&8&l> &c&l-&c $&l"),
    ATM(" &3&lATM&8&l> "),
    BANK(" &3&lBANK&8&l> "),
    BANK_ADD("&3&lBANK&8&l> &a&l+&a $&l"),
    BANK_TAKE("&3&lBANK&8&l> &c&l-&c $&l"),
    GTM(" &7&l" + (Bungee.GTM ? "GTM" : "GTA") + "&8&l> "),
    PREFS(" &5&lPREFS&8&l> "),
    AMMUNATION(" &9&lAMMU&4&lNATION&8&l> "),
    WANTED(" &c&lWANTED&8&l> "),
    TAXI(" &e&lTAXI&8&l> "),
    SHOP(" &a&lSHOP&8&l> "),
    TRASH_CAN(" &7&lTRASH CAN&8&l> "),
    HEY(" &c&lHEY&8&l> "),
    COMBATTAG(" &7&lCOMBATTAG&8&l> "),
    GUARDPETS("&c&l GUARD PETS&8&l> "),
    BOUNTIES(" &5&lBOUNTIES&8&l> "),
    VOTE(" &e&lVOTE&8&l> "),
    TOKEN_SHOP(" &e&lTOKEN SHOP&8&l> "),
    KITS(" &b&lKITS&8&l> "),
    RANKUP(" &a&lRANKUP&8&l> "),
    RANKS(" &a&lRANKS&8&l> "),
    JOBS(" &3&lJOBS&8&l> "),
    COP_MODE(" &3&lCOP MODE&8&l> "),
    HITMAN_MODE(" &8&lHITMAN MODE> "),
    GPS(" &7&lGPS&8&l> "),
    HOUSES(" &3&lHOUSES&8&l> "),
    TUTORIALS(" &2&lTUTORIALS&8&l> "),
    GANGS(" &a&lGANGS&8&l> "),
    NOPERM("&cYou want me to clap yo ass? You ain't got permission for this shit!"),
    NOTPLAYER("&cDawg you ain't a player!"),
    STAFF(" &c&lSTAFF&8&l> "),
    HELP(" &d&lHELP&8&l> "),
    GSPY(" &a&lGSPY&8&l> "),
    PERMS(" &c&lGPERMS&8&l> "),
    VERIFICATION(" &c&lVERIFICATION&8&l> ");

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

    public TextComponent ft(String s) {
        return Utils.ft(this.s + s);
    }

    public TextComponent st() {
        return Utils.ft(this.s);
    }

}
