package com.p1nero.dialog_lib.util;

import com.p1nero.dialog_lib.DialogueLib;
import com.p1nero.dialog_lib.api.block.BlockDialogueExtension;
import com.p1nero.dialog_lib.api.block.IBlockDialogueExtension;
import com.p1nero.dialog_lib.api.entity.EntityDialogueExtension;
import com.p1nero.dialog_lib.api.entity.IEntityDialogueExtension;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.util.*;

/**
 * Copy from <a href="https://github.com/mezz/JustEnoughItems/blob/1.16/src/main/java/mezz/jei/util/AnnotatedInstanceUtil.java">...</a>
 */
@SuppressWarnings("rawtypes")
public final class AnnotatedInstanceUtil {

    public static List<IEntityDialogueExtension> getModEntityExtensions() {
        return getInstances(EntityDialogueExtension.class, IEntityDialogueExtension.class);
    }

    public static List<IBlockDialogueExtension> getModBlockExtensions() {
        return getInstances(BlockDialogueExtension.class, IBlockDialogueExtension.class);
    }

    @SuppressWarnings("SameParameterValue")
    private static <T> List<T> getInstances(Class<?> annotationClass, Class<T> instanceClass) {
        Type annotationType = Type.getType(annotationClass);
        List<ModFileScanData> allScanData = ModList.get().getAllScanData();
        Set<String> extensionClassNames = new LinkedHashSet<>();
        for (ModFileScanData scanData : allScanData) {
            for (ModFileScanData.AnnotationData data : scanData.getAnnotations()) {
                if (Objects.equals(data.annotationType(), annotationType)) {
                    String memberName = data.memberName();
                    extensionClassNames.add(memberName);
                }
            }
        }
        List<T> instances = new ArrayList<>();
        for (String className : extensionClassNames) {
            try {
                Class<?> asmClass = Class.forName(className);
                Class<? extends T> asmInstanceClass = asmClass.asSubclass(instanceClass);
                T instance = asmInstanceClass.newInstance();
                instances.add(instance);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | LinkageError e) {
                DialogueLib.LOGGER.error("Failed to load: {}", className, e);
            }
        }
        return instances;
    }
}
