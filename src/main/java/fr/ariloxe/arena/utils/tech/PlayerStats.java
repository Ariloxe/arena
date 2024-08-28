package fr.ariloxe.arena.utils.tech;

import fr.kanao.core.PyraliaCore;
import fr.kanao.core.tech.mongo.MongoProfileCreator;

import java.util.UUID;

/**
 * @author Ariloxe
 */
public class PlayerStats {

    private final UUID uuid;
    private int kills;
    private int deaths;
    private String editInventory = "";

    public PlayerStats(UUID uuid) {
        this.uuid = uuid;
        PyraliaCore.getInstance().getMongoManager().contains(ArenaStatsCollection.class, uuid.toString()).thenAccept(aBoolean -> {
            if(aBoolean){
                PyraliaCore.getInstance().getMongoManager().getAllFromCollection(ArenaStatsCollection.class, uuid.toString()).thenAccept(arenaProfile -> {
                    this.kills = (int) arenaProfile.get("kills");
                    this.deaths = (int) arenaProfile.get("deaths");
                    if(arenaProfile.containsField("inventory"))
                        this.editInventory = (String) arenaProfile.get("inventory");
                }).exceptionally(throwable -> {
                    throwable.printStackTrace();
                    return null;
                });
            } else {
                this.kills = 0;
                this.deaths = 0;
            }
        });
    }

    public int getDeaths() {
        return deaths;
    }

    public int getKills() {
        return kills;
    }

    public double getKD(){
        return (double) kills/deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public String getEditInventory() {
        return editInventory;
    }

    public void setEditInventory(String editInventory) {
        this.editInventory = editInventory;
    }

    public void disconnect(){

        PyraliaCore.getInstance().getMongoManager().contains(ArenaStatsCollection.class, uuid.toString()).thenAccept(aBoolean -> {
            if(aBoolean){
                PyraliaCore.getInstance().getMongoManager().setFromCollection(ArenaStatsCollection.class, uuid.toString(), "kills", kills);
                PyraliaCore.getInstance().getMongoManager().setFromCollection(ArenaStatsCollection.class, uuid.toString(), "deaths", deaths);
                PyraliaCore.getInstance().getMongoManager().setFromCollection(ArenaStatsCollection.class, uuid.toString(), "inventory", editInventory);
            } else {
                MongoProfileCreator mongoProfileCreator = new MongoProfileCreator(uuid.toString());
                mongoProfileCreator.addEntry("kills", kills);
                mongoProfileCreator.addEntry("deaths", deaths);
                mongoProfileCreator.addEntry("inventory", editInventory);
                PyraliaCore.getInstance().getMongoManager().createProfile(ArenaStatsCollection.class, mongoProfileCreator);
            }
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });

    }
}
