//
// MessagePack for Java
//
// Copyright (C) 2009 - 2013 FURUHASHI Sadayuki
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//
package org.msgpack.template.builder;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.msgpack.annotation.Beans;
import org.msgpack.annotation.Ignore;
import org.msgpack.annotation.Index;
import org.msgpack.annotation.Message;
import org.msgpack.annotation.MessagePackBeans;
import org.msgpack.annotation.MessagePackMessage;
import org.msgpack.annotation.MessagePackOrdinalEnum;
import org.msgpack.annotation.NotNullable;
import org.msgpack.annotation.Optional;
import org.msgpack.annotation.OrdinalEnum;
import org.msgpack.template.FieldList;
import org.msgpack.template.FieldOption;
import org.msgpack.template.Template;
import org.msgpack.template.TemplateRegistry;

public abstract class AbstractTemplateBuilder implements TemplateBuilder {

    protected TemplateRegistry registry;

    protected AbstractTemplateBuilder(final TemplateRegistry registry) {
        this.registry = registry;
    }

    @Override
    public <T> Template<T> buildTemplate(final Type targetType)
            throws TemplateBuildException {
        @SuppressWarnings("unchecked")
        final Class<T> targetClass = (Class<T>) targetType;
        checkClassValidation(targetClass);
        final FieldOption fieldOption = getFieldOption(targetClass);
        final FieldEntry[] entries = toFieldEntries(targetClass, fieldOption);
        return buildTemplate(targetClass, entries);
    }

    @Override
    public <T> Template<T> buildTemplate(final Class<T> targetClass,
            final FieldList fieldList) throws TemplateBuildException {
        checkClassValidation(targetClass);
        final FieldEntry[] entries = toFieldEntries(targetClass, fieldList);
        return buildTemplate(targetClass, entries);
    }

    protected abstract <T> Template<T> buildTemplate(
            final Class<T> targetClass, final FieldEntry[] entries);

    protected void checkClassValidation(final Class<?> targetClass) {
        if (Modifier.isAbstract(targetClass.getModifiers())) {
            throw new TemplateBuildException(
                    "Cannot build template for abstract class: "
                            + targetClass.getName());
        }
        if (targetClass.isInterface()) {
            throw new TemplateBuildException(
                    "Cannot build template for interface: "
                            + targetClass.getName());
        }
        if (targetClass.isArray()) {
            throw new TemplateBuildException(
                    "Cannot build template for array class: "
                            + targetClass.getName());
        }
        if (targetClass.isPrimitive()) {
            throw new TemplateBuildException(
                    "Cannot build template of primitive type: "
                            + targetClass.getName());
        }
    }

    protected FieldOption getFieldOption(final Class<?> targetClass) {
        final Message m = targetClass.getAnnotation(Message.class);
        if (m == null) {
            return FieldOption.DEFAULT;
        }
        final MessagePackMessage mpm = targetClass
                .getAnnotation(MessagePackMessage.class);
        if (mpm == null) {
            return FieldOption.DEFAULT;
        }
        // TODO #MN
        return m.value();
    }

    private FieldEntry[] toFieldEntries(final Class<?> targetClass,
            final FieldList flist) {
        final List<FieldList.Entry> src = flist.getList();
        final FieldEntry[] entries = new FieldEntry[src.size()];
        for (int i = 0; i < src.size(); i++) {
            final FieldList.Entry s = src.get(i);
            if (s.isAvailable()) {
                try {
                    entries[i] = new DefaultFieldEntry(
                            targetClass.getDeclaredField(s.getName()),
                            s.getOption());
                } catch (final SecurityException e) {
                    throw new TemplateBuildException(e);
                } catch (final NoSuchFieldException e) {
                    throw new TemplateBuildException(e);
                }
            } else {
                entries[i] = new DefaultFieldEntry();
            }
        }
        return entries;
    }

    protected FieldEntry[] toFieldEntries(final Class<?> targetClass,
            final FieldOption from) {
        final Field[] fields = getFields(targetClass);

        /*
         * index:
         *
         * @Index(0)
         * int field_a; // 0
         * int field_b; // 1
         * @Index(3)
         * int field_c; // 3
         * int field_d; // 4
         * @Index(2)
         * int field_e; // 2
         * int field_f; // 5
         */
        final List<FieldEntry> indexed = new ArrayList<FieldEntry>();
        int maxIndex = -1;
        for (final Field f : fields) {
            final FieldOption opt = getFieldOption(f, from);
            if (opt == FieldOption.IGNORE) {
                // skip
                continue;
            }

            final int index = getFieldIndex(f, maxIndex);
            if (indexed.size() > index && indexed.get(index) != null) {
                throw new TemplateBuildException("duplicated index: " + index);
            }
            if (index < 0) {
                throw new TemplateBuildException("invalid index: " + index);
            }

            while (indexed.size() <= index) {
                indexed.add(null);
            }
            indexed.set(index, new DefaultFieldEntry(f, opt));

            if (maxIndex < index) {
                maxIndex = index;
            }
        }

        final FieldEntry[] entries = new FieldEntry[maxIndex + 1];
        for (int i = 0; i < indexed.size(); i++) {
            final FieldEntry e = indexed.get(i);
            if (e == null) {
                entries[i] = new DefaultFieldEntry();
            } else {
                entries[i] = e;
            }
        }
        return entries;
    }

    private Field[] getFields(final Class<?> targetClass) {
        // order: [fields of super class, ..., fields of this class]
        final List<Field[]> succ = new ArrayList<Field[]>();
        int total = 0;
        for (Class<?> c = targetClass; c != Object.class; c = c.getSuperclass()) {
            final Field[] fields = c.getDeclaredFields();
            total += fields.length;
            succ.add(fields);
        }
        final Field[] result = new Field[total];
        int off = 0;
        for (int i = succ.size() - 1; i >= 0; i--) {
            final Field[] fields = succ.get(i);
            System.arraycopy(fields, 0, result, off, fields.length);
            off += fields.length;
        }
        return result;
    }

    private FieldOption getFieldOption(final Field field, final FieldOption from) {
        final int mod = field.getModifiers();
        // default mode:
        // transient, static, final : Ignore
        // primitive type : NotNullable
        // reference type : Ignore
        if (Modifier.isStatic(mod) || Modifier.isFinal(mod)
                || Modifier.isTransient(mod)) {
            return FieldOption.IGNORE;
        }

        if (isAnnotated(field, Ignore.class)) {
            return FieldOption.IGNORE;
        } else if (isAnnotated(field, Optional.class)) {
            return FieldOption.OPTIONAL;
        } else if (isAnnotated(field, NotNullable.class)) {
            return FieldOption.NOTNULLABLE;
        }

        if (from != FieldOption.DEFAULT) {
            return from;
        }

        if (field.getType().isPrimitive()) {
            return FieldOption.NOTNULLABLE;
        } else {
            return FieldOption.OPTIONAL;
        }
    }

    private int getFieldIndex(final Field field, final int maxIndex) {
        final Index a = field.getAnnotation(Index.class);
        if (a == null) {
            return maxIndex + 1;
        } else {
            return a.value();
        }
    }

    @Override
    public void writeTemplate(final Type targetType, final String directoryName) {
        throw new UnsupportedOperationException(targetType.toString());
    }

    @Override
    public <T> Template<T> loadTemplate(final Type targetType) {
        return null;
    }

    public static boolean isAnnotated(final Class<?> targetClass,
            final Class<? extends Annotation> with) {
        return targetClass.getAnnotation(with) != null;
    }

    public static boolean isAnnotated(final AccessibleObject accessibleObject,
            final Class<? extends Annotation> with) {
        return accessibleObject.getAnnotation(with) != null;
    }

    public static boolean matchAtClassTemplateBuilder(
            final Class<?> targetClass, final boolean hasAnnotation) {
        if (hasAnnotation) {
            return AbstractTemplateBuilder.isAnnotated(targetClass,
                    Message.class)
                    || AbstractTemplateBuilder.isAnnotated(targetClass,
                            MessagePackMessage.class);
        } else {
            return !targetClass.isEnum() && !targetClass.isInterface();
        }
    }

    public static boolean matchAtBeansClassTemplateBuilder(
            final Type targetType, final boolean hasAnnotation) {
        final Class<?> targetClass = (Class<?>) targetType;
        if (hasAnnotation) {
            return AbstractTemplateBuilder.isAnnotated((Class<?>) targetType,
                    Beans.class)
                    || AbstractTemplateBuilder.isAnnotated(
                            (Class<?>) targetType, MessagePackBeans.class);
        } else {
            return !targetClass.isEnum() || !targetClass.isInterface();
        }
    }

    public static boolean matchAtArrayTemplateBuilder(
            final Class<?> targetClass, final boolean hasAnnotation) {
        final Type type = targetClass;
        if (type instanceof GenericArrayType) {
            return true;
        }
        return targetClass.isArray();
    }

    public static boolean matchAtOrdinalEnumTemplateBuilder(
            final Class<?> targetClass, final boolean hasAnnotation) {
        if (hasAnnotation) {
            return AbstractTemplateBuilder.isAnnotated(targetClass,
                    OrdinalEnum.class)
                    || AbstractTemplateBuilder.isAnnotated(targetClass,
                            MessagePackOrdinalEnum.class);
        } else {
            return targetClass.isEnum();
        }
    }
}
