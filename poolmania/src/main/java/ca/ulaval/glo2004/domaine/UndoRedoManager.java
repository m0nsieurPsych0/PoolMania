package ca.ulaval.glo2004.domaine;

import ca.ulaval.glo2004.utils.UtilitaireSerialization;

import java.util.ArrayList;
import java.util.Objects;

public class UndoRedoManager<T> {

    private ArrayList<byte[]> listObject;
    private int index = 0;

    public UndoRedoManager(T objet) {
        listObject = new ArrayList<>();
        listObject.add(UtilitaireSerialization.convertObjectToBytes(objet));
    }

    public void addState(T objet) {
        index++;
        //https://stackoverflow.com/questions/22802232/remove-all-elements-from-a-list-after-a-particular-index
        listObject.subList(index, listObject.size()).clear();
        listObject.add(UtilitaireSerialization.convertObjectToBytes(objet));
    }

    public void removeState() {
        if (index > 0) {
            index--;
            listObject.remove(listObject.size()-1);
        }
    }

    public T undo(){
        if(index > 0){
            index--;
        }
        return (T)UtilitaireSerialization.convertBytesToObject(listObject.get(index));
    }

    public T redo(){
        if(index < listObject.size() - 1){
            index++;
        }
        return (T) UtilitaireSerialization.convertBytesToObject(listObject.get(index));
    }
}
