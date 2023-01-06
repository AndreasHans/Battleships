package org.battleships;

import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.Space;

public class main {

    public static void main(String[] argv) throws InterruptedException {
        Space inbox = new SequentialSpace();

        inbox.put("Hello HI!");
        Object[] tuple = inbox.get(new FormalField(String.class));
        System.out.println(tuple[0]);

    }

}
