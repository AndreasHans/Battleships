package org.battleships;

import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.Space;

/**
 * A simple HelloWorld program.
 *
 *
 * @author Michele Loreti
 *
 */
public class main {

    public static void main(String[] argv) throws InterruptedException {
        Space inbox = new SequentialSpace();

        inbox.put("Hello World!");
        Object[] tuple = inbox.get(new FormalField(String.class));
        System.out.println(tuple[0]);

    }

}
