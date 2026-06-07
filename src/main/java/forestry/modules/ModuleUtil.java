package forestry.modules;

import forestry.Forestry;
import forestry.modules.features.FeatureProvider;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Consumer;

public class ModuleUtil {
	private static final HashMap<String, BusGroup> MOD_BUS_GROUPS = new HashMap<>();

	public static void registerModBusGroup(String modId, BusGroup modBusGroup) {
		MOD_BUS_GROUPS.put(modId, modBusGroup);
	}

	public static BusGroup getModBusGroup(String modid) {
		return MOD_BUS_GROUPS.computeIfAbsent(modid, ModuleUtil::lookupModBusGroup);
	}

	public static void loadFeatureProviders() {
		forEachAnnotated(Type.getType(FeatureProvider.class), klass -> Forestry.LOGGER.debug("Loaded feature provider {}", klass));
	}

	static void forEachAnnotated(Type annotationType, Consumer<Class<?>> action) {
		for (ModFileScanData scanData : ModList.getAllScanData()) {
			Set<ModFileScanData.AnnotationData> annotationData = scanData.getAnnotations();

			for (ModFileScanData.AnnotationData data : annotationData) {
				if (!data.annotationType().equals(annotationType)) {
					continue;
				}

				String className = data.memberName();
				try {
					Class<?> klass = Class.forName(className);
					action.accept(klass);
				} catch (ReflectiveOperationException e) {
					throw new RuntimeException("Failed to load annotated member " + className, e);
				}
			}
		}
	}

	private static BusGroup lookupModBusGroup(String modid) {
		ModContainer modContainer = ModList.getModContainerById(modid).orElseThrow();

		try {
			Method method = modContainer.getClass().getMethod("getModBusGroup");
			return (BusGroup) method.invoke(modContainer);
		} catch (ReflectiveOperationException ignored) {
		}

		for (Class<?> type = modContainer.getClass(); type != Object.class; type = type.getSuperclass()) {
			for (Field field : type.getDeclaredFields()) {
				String name = field.getName();
				if ((name.equals("modBusGroup") || name.equals("eventBus") || name.equals("modBus"))
						&& BusGroup.class.isAssignableFrom(field.getType())) {
					field.setAccessible(true);
					try {
						return (BusGroup) field.get(modContainer);
					} catch (IllegalAccessException e) {
						throw new RuntimeException("Failed to read mod bus group field for '" + modid + "'", e);
					}
				}
			}
		}

		throw new RuntimeException("Failed to obtain mod bus group for '" + modid + "'");
	}
}
