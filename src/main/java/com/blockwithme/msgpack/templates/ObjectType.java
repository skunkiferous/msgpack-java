/*
 * Copyright (C) 2013 Sebastien Diot.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blockwithme.msgpack.templates;

/**
 * When serialising with MessagePack, we have 4 kinds of possible format
 * available to us, for each type we want to write:
 *
 * array
 * map
 * primitive
 * raw (uncommon).
 *
 * It all boils down to this: when using a map to represent an object, the each
 * property must be written as exactly one value. Therefore, you cannot simply
 * inline the definition of your property as multiple values. So each type must
 * be able to write itself as exactly one value. Therefore, most types must use
 * a container (either array or map) to store themselves. The exceptions are
 * single-value types: raw and primitive. But we can only write those directly
 * as raw or primitive if the exact type will be known when reading them back.
 *
 * Normally, objects are not raw, with the exception of String which uses raw
 * (in fact, String is the "default raw", when not wrapped in an array that
 * gives us the actual object type).
 *
 * Both array and map are appropriate for most normal objects. It depends on
 * if we assume many object properties are going to be "default" or not.
 *
 * Array is preferred the default, for the case when we would otherwise have a
 * primitive or raw, but we need the additional type ID information too.
 *
 * Primitive objects are things like java.util.Date, which are made from one
 * primitive value. Most objects have multiple properties, and therefore cannot
 * be recoded as primitives. In fact, primitives are a special case of array,
 * where the array size is 1.
 *
 * @author monster
 */
public enum ObjectType {
    ARRAY, MAP, PRIMITIVE, RAW
}
