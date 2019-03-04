/*
 * Copyright (c) 2019 www.foxtail.cc All rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package identity.foxtail.core.domain.model.permission;

import java.util.Objects;

/***
 * @author <a href="www.foxtail.cc/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 2019-01-19
 */
public class Processor {
    private Fuel fuel;
    private Engine engine;

    public Processor(Engine engine, Fuel fuel) {
        setEngine(engine);
        setFuel(fuel);
    }

    /**
     * Gets the value of fuel.
     *
     * @return the value of fuel
     */
    public Fuel fuel() {
        return fuel;
    }

    /**
     * Sets the value of code.
     */
    private void setFuel(Fuel fuel) {
        Objects.requireNonNull(fuel, "fuel is required");
        if (engine.isQualifiedFuel(fuel))
            throw new IllegalArgumentException("Not qualified fuel");
        this.fuel = fuel;
    }

    /**
     * Gets the value of engine.
     *
     * @return the value of engine
     */
    public Engine engine() {
        return engine;
    }

    /**
     * Sets the value of code.
     */
    private void setEngine(Engine engine) {
        this.engine = Objects.requireNonNull(engine, "engine is required");
    }

    @Override
    public String toString() {
        return "Processor{" +
                "fuel=" + fuel +
                ", engine=" + engine +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Processor processor = (Processor) o;

        if (fuel != null ? !fuel.equals(processor.fuel) : processor.fuel != null) return false;
        return engine != null ? engine.equals(processor.engine) : processor.engine == null;
    }

    @Override
    public int hashCode() {
        int result = fuel != null ? fuel.hashCode() : 0;
        result = 31 * result + (engine != null ? engine.hashCode() : 0);
        return result;
    }
}
