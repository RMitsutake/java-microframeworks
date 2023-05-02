
package me.vscode.se.database;

/**
 * POJO representing Pokemon.
 *
 * Pokemon, and Pokemon character names are trademarks of Nintendo.
 */
public class Pokemon {

    private int id;
    private String name;
    private int idType;
    private double weight;  
    
    /**
     * Default constructor.
     */
    public Pokemon() {
        // JSON-B
    }

    /**
     * Create pokemon with name and type.
     *
     * @param id id of the beast
     * @param name name of the beast
     * @param idType id of beast type
     * @param weight weight of the beast
     */
    public Pokemon(int id, String name, int idType, double weight) {
        this.id = id;
        this.name = name;
        this.idType = idType;
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = idType;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

}
