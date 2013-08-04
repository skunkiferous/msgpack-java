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
 * Defines how the instances of a template should be tracked.
 *
 * @author monster
 */
public enum TrackingType {
    /**
     * Objects are compared with equality. Normally, only immutable objects
     * should fall in this category.
     */
    EQUALITY,
    /**
     * Objects are compared with identity. Normally, all mutable objects
     * should fall in this category.
     */
    IDENTITY,
    /**
     * Objects are not tracked at all. This is a performance optimization;
     * rarely needed. Eliminating tracking saves CPU and RAM, but if the
     * object should be referenced more then once, it would be serialized
     * multiple times (and come back out as multiple instances)!
     */
    DO_NOT_TRACK
}