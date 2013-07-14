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
 * When serialising with MessagePack, we have 3 kinds of possible format
 * available to us, for each type we want to write: array, map, and raw
 * (uncommon). Normally, objects use either array or map, with the exception
 * of String which uses raw (in fact, String is the "default raw", when not
 * wrapped in an array that gives us the actual object type).
 *
 * @author monster
 */
public enum ObjectType {
    ARRAY, MAP, RAW
}
