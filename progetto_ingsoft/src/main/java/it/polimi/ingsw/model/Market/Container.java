package it.polimi.ingsw.model.Market;

public class Container{

    /*
      this String indicates which type of resource is stored in this container
     */
    private Resource resource;

    /*
      this variable indicates if this container
      can store a resource or is occupied
     */
    private boolean empty;


    /**
     * @return return the actual value of the attribute Empty
     */
    public boolean isEmpty(){
        return this.empty;
    }

    /**
     * this constructor create an empty container initializing attributes
     */
    public Container() {
        this.resource=null;
        this.empty=true;
    }

    /**
     * @return the actual Resource stored in container
     */
    public Resource getResource() {
        return resource;
    }


    /**
     * this method put a new Resource in container modifying attributes
     * @param resource is the Resource to store in container
     */
    public boolean fillContainer  (Resource resource) {
        this.resource=resource;
        this.empty = false;
        return true;
    }

    /**
     * this method modifies attributes to indicate that
     * the resource in this container has been taken by the player
     * and the container has been emptied
     * @return the the Resource taken from the container
     */
    public Resource takeResource() throws EmptyContainerException{
        if(this.empty==false) {
            this.empty = true;
            return this.resource;
        }
        throw new EmptyContainerException("Il container è vuoto, non è possibile prendere alcuna risorsa");
    }

}
