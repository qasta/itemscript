/*
 * Copyright © 2010, Data Base Architects, Inc. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright notice,
 *       this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *     * Neither the names of Kalinda Software, DBA Software, Data Base Architects, Itemscript
 *       nor the names of its contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 * Author: Jacob Davies
 */

package org.itemscript.test;

import org.itemscript.core.foundries.ItemscriptFoundry;
import org.itemscript.core.foundries.JsonFactory;
import org.itemscript.core.foundries.JsonFoundry;
import org.itemscript.core.values.JsonObject;
import org.junit.Test;

public class JsonFoundryTest extends ItemscriptTestBase {
    public interface Animal {
        public String name();

        public String species();
    }
    class Cat implements Animal {
        private final String name;
        private boolean indoor;

        public Cat(String name) {
            this.name = name;
            this.indoor = false;
        }

        public boolean indoor() {
            return indoor;
        }

        public String name() {
            return name;
        }

        public void setIndoor(boolean indoor) {
            this.indoor = indoor;
        }

        public String species() {
            return "cat";
        }
    }
    class CatFactory implements JsonFactory<Animal> {
        public Cat create(JsonObject parameters) {
            String name = parameters.getString("name");
            Cat cat = new Cat(name);
            if (parameters.hasBoolean("indoor")) {
                cat.setIndoor(parameters.getBoolean("indoor"));
            }
            return cat;
        }
    }
    class Dog implements Animal {
        private final String name;
        private boolean shaggy;

        public Dog(String name) {
            this.name = name;
            this.shaggy = false;
        }

        public String name() {
            return name;
        }

        public void setShaggy(boolean shaggy) {
            this.shaggy = shaggy;
        }

        public boolean shaggy() {
            return shaggy;
        }

        public String species() {
            return "dog";
        }
    }
    class DogFactory implements JsonFactory<Animal> {
        public Dog create(JsonObject parameters) {
            String name = parameters.getString("name");
            Dog dog = new Dog(name);
            if (parameters.hasBoolean("shaggy")) dog.setShaggy(parameters.getBoolean("shaggy"));
            return dog;
        }
    }

    @Test
    public void testFoundry() {
        JsonFoundry<Animal> animalFoundry = new ItemscriptFoundry<Animal>(system(), "/AnimalFoundry", "species");
        animalFoundry.put("Cat", new CatFactory());
        animalFoundry.put("Dog", new DogFactory());
        JsonObject catJson = system().getObject("classpath:org/itemscript/test/cat.json");
        Animal cat = animalFoundry.create(catJson);
        assertTrue(cat instanceof Cat);
        assertTrue(((Cat) cat).indoor());
        assertEquals("Victoria", cat.name());
        JsonObject dogJson = system().getObject("classpath:org/itemscript/test/dog.json");
        Animal dog = animalFoundry.create(dogJson);
        assertTrue(dog instanceof Dog);
        assertTrue(((Dog) dog).shaggy());
        assertEquals("Bella", dog.name());
        Animal anotherCat = animalFoundry.create("Cat");
        assertTrue(anotherCat instanceof Cat);
        assertNull(anotherCat.name());
        assertFalse(((Cat) anotherCat).indoor());
    }
}