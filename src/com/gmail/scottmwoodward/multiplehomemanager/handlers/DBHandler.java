package com.gmail.scottmwoodward.multiplehomemanager.handlers;

import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Queue;

import com.gmail.scottmwoodward.multiplehomemanager.MultipleHomeManager;

import lib.PatPeter.SQLibrary.SQLite;

public class DBHandler {
    private SQLite sql;
    private MultipleHomeManager plugin;

    public DBHandler(MultipleHomeManager plugin){
        this.plugin = plugin;
    }

    public void sqlConnection(){
        sql = new SQLite(plugin.getLogger(),"", "Homes", plugin.getDataFolder().getAbsolutePath());
        try{
            sql.open();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sqlTableCheck(){
        if(sql.checkTable("homes")){
            plugin.getLogger().info("Database exists, loading");
            convertDB();

            return;
        }
        else{
            plugin.getLogger().info("TABLE BEING CREATED");
            sql.createTable("CREATE TABLE homes(id INTEGER PRIMARY KEY AUTOINCREMENT, playername VARCHAR(20), world VARCHAR(20), homenumber INTEGER, x DOUBLE, y DOUBLE, z DOUBLE)");
        }
    }

    public void convertDB(){
        plugin.getLogger().info("Converting database to version 1.1.0");
        String lookup = "SELECT * FROM homes";
        ResultSet r = sql.query(lookup);
        Queue<String> needsConverted = new LinkedList<String>();
        try{
            while(r.next()){

                needsConverted.add(r.getString("playername"));
            }
            r.close();
            while(!needsConverted.isEmpty()){
                String update = "UPDATE homes SET playername='"+needsConverted.peek().toLowerCase()+"' WHERE playername='"+needsConverted.poll()+"'";
                sql.query(update);
                r.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void insert(String playerName, String world, int homeNumber, double x, double y, double z){
        String insert = "INSERT INTO homes(playername, world, homenumber, x, y, z) VALUES ('";
        insert = insert + playerName.toLowerCase() +"', '"+world+"', "+String.valueOf(homeNumber)+", "+String.valueOf(x)+", "+String.valueOf(y)+", "+String.valueOf(z)+")";
        ResultSet r = sql.query(insert);
        try{
            if(r!=null){
                r.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public void update(String playerName, String world, int homeNumber, double x, double y, double z){
        String update = "UPDATE homes SET world='"+world+"', x="+String.valueOf(x)+", y="+String.valueOf(y)+", z="+String.valueOf(z)+" WHERE playername='"+playerName.toLowerCase()+"' AND homenumber="+String.valueOf(homeNumber);
        ResultSet r = sql.query(update);
        try{
            if(r!=null){
                r.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String listHomes(String playerName){
        String lookup = "SELECT * FROM homes WHERE playername='"+playerName.toLowerCase()+"'";
        ResultSet r = sql.query(lookup);
        String result = "";
        try{
            while(r.next()){

                result+= "Home "+String.valueOf(r.getInt("homenumber"))+": ("+String.valueOf(roundTwoDecimals(r.getDouble("x")))+", "+String.valueOf(roundTwoDecimals(r.getDouble("y")))+", "+String.valueOf(roundTwoDecimals(r.getDouble("z")))+") in "+r.getString("world")+"\n";
            }
            r.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public boolean rowExist(String player, int homeNumber){
        String lookup = "SELECT playername, homenumber FROM homes WHERE  playername='"+player.toLowerCase()+"' AND homenumber="+String.valueOf(homeNumber);
        ResultSet r = sql.query(lookup);

        try{
            if(r.next()){
                r.close();
                return true;
            }
            /*a = (r.getString(1).equalsIgnoreCase(player));
			b = (r.getString(2).equalsIgnoreCase(String.valueOf(homeNumber)));
			if(a&&b){
				r.close();
				return true;
			}*/
            r.close();
            return false;
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public double getComponent(String player, int homeNumber, String id){
        String lookup = "SELECT "+id+" FROM homes WHERE playername='"+player.toLowerCase()+"' AND homenumber="+String.valueOf(homeNumber);
        double value;
        ResultSet r;
        try{
            r = sql.query(lookup);
            value = r.getDouble(1);
            if(r!=null){
                r.close();
            };
            return value;

        } catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public String getWorld(String player, int homeNumber){
        String lookup = "SELECT world FROM homes WHERE playername='"+player.toLowerCase()+"' AND homenumber="+String.valueOf(homeNumber);
        String value;
        ResultSet r;
        try{
            r = sql.query(lookup);
            value = r.getString(1);
            if(r!=null){
                r.close();
            };
            return value;

        } catch (Exception e){
            e.printStackTrace();
        }
        return "invalid";
    }

    public double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }	

    public void sqlClose(){
        try{
            sql.getConnection().close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void unset(String playername, int homeNumber) {
        String remove ="DELETE FROM homes WHERE playername='"+playername.toLowerCase()+"' AND homenumber="+String.valueOf(homeNumber);
        ResultSet r = sql.query(remove);
        try{
            if(r!=null){
                r.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
