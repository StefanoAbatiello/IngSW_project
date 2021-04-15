package it.polimi.ingsw.model.Market;

import it.polimi.ingsw.exceptions.EmptyContainerException;
import it.polimi.ingsw.model.Resource;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ContainerTest {

    /*
    this Test is implemented to check if method isEmpty return true in case of an Empty container
     */
    @Test
    void isEmptyTest(){
        Container container = new Container();
        assertTrue(container.isEmpty());
    }

    /*
    this Test is implemented to check if method isEmpty return false in case of an occupied container
    */
    @Test
    void isNotEmptyTest() {
        Container container = new Container();
        container.fillContainer(Resource.SHIELD);
        assertFalse(container.isEmpty());
    }

    /*
    this Test is implemented to check if method fillContainer modifies container's attribute correctly
    */
    @Test
    void fillContainerTest() {
        Container container = new Container();
        container.fillContainer(Resource.SHIELD);
        assertEquals(Resource.SHIELD,container.getResource());
    }

    /*
    this Test is implemented to check if method takeResource empties container correctly
    */
    @Test
    void emptyingTest() throws EmptyContainerException {
        Container container = new Container();
        container.fillContainer(Resource.SHIELD);
        container.takeResource();
        assertTrue(container.isEmpty());
    }

    /*
    this Test is implemented to check if method takeResource return Resource correctly
    */
    @Test
    void takingResourceTest() throws EmptyContainerException{
        Container container = new Container();
        container.fillContainer(Resource.SHIELD);
        assertEquals(Resource.SHIELD,container.takeResource());
    }

    /*
    this Test is implemented to check if method takeResource throws the exception in case of an Empty container
     */
    @Test
    void takingResourceFromEmptyContainerTest(){
        Container container = new Container();
        assertThrows(EmptyContainerException.class,()->container.takeResource());
    }

    /*
    this Test is implemented to check if iterating methods fillContainer and takeResource the attributes are modified correctly
    */
    @Test
    void refillingContainerTest() throws EmptyContainerException{
        Container container = new Container();
        container.fillContainer(Resource.SHIELD);
        container.takeResource();
        container.fillContainer(Resource.COIN);
        assertEquals(Resource.COIN,container.takeResource());
    }

    /*
   this Test is implemented to check if iterating methods fillContainer and takeResource the attributes are modified correctly
   */
    @Test
    void reEmptyingContainerTest() throws EmptyContainerException {
        Container container = new Container();
        container.fillContainer(Resource.SHIELD);
        container.takeResource();
        container.fillContainer(Resource.COIN);
        container.takeResource();
        assertTrue(container.isEmpty());
    }

    /*
    this Test is implemented to check if method takeResource return a null pointer in case of an Empty container
    */
    /*@Test
    void takeNotExistingResourceTest() {
        Container container = new Container();
        assertThrows(EmptyContainerException.class, container::takeResource );
    }
    */
}