package jeql.man;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import jeql.api.annotation.Metadata;
import jeql.util.ClassUtil;

public class ManUtil {
	public static String description(Class<?> clz) {
		Metadata doc = clz.getAnnotation(Metadata.class);
		if (doc != null)
			return doc.description();
		return "";
	}

	public static String name(Annotation[] anno) {
		for (int i = 0; i < anno.length; i++) {
			if (anno[i] instanceof Metadata) {
				Metadata doc = (Metadata) anno[i];
				if (doc != null)
					return doc.name();
			}
		}
		return "";
	}

	public static String name(Method m) {
		Metadata doc = m.getAnnotation(Metadata.class);
		return (doc == null) ? "" : doc.name();
	}

	public static String description(Annotation[] anno) {
		for (int i = 0; i < anno.length; i++) {
			if (anno[i] instanceof Metadata) {
				Metadata doc = (Metadata) anno[i];
				if (doc != null)
					return doc.description();
			}
		}
		return "";
	}

	public static String description(Method m) {
		Metadata doc = m.getAnnotation(Metadata.class);
		String desc = (doc == null) ? "" : doc.description();
		return desc;
	}
	public static String values(Method m) {
		Metadata doc = m.getAnnotation(Metadata.class);
		String values = "";
		if (doc != null) {
			String[] vals = doc.values();
			if (vals.length > 0) {
				values = "[ Values: " + String.join(", ", vals) + " ]";
			}
		}
		return values;
	}

	public static boolean isMultiple(Method m) {
		Metadata doc = m.getAnnotation(Metadata.class);
		return (doc == null) ? false : doc.isMultiple();
	}

	public static boolean isRequired(Method m) {
		Metadata doc = m.getAnnotation(Metadata.class);
		return (doc == null) ? false : doc.isRequired();
	}

	public static String returnType(Method meth) {
		return ClassUtil.classname(meth.getReturnType().getName());
	}

	public static String paramTypeList(Method meth) {
		StringBuffer pd = new StringBuffer();
		Class[] param = meth.getParameterTypes();
		for (int i = 0; i < param.length; i++) {
			if (i > 0)
				pd.append(", ");
			pd.append(ClassUtil.classname(param[i]));
		}
		return pd.toString();
	}

	public static String functionParamDoc(Method meth) {
		StringBuffer pd = new StringBuffer();
		Class[] param = meth.getParameterTypes();
		Annotation[][] anno = meth.getParameterAnnotations();
		for (int i = 0; i < param.length; i++) {
			if (i > 0)
				pd.append("\n");

			String annoName = name(anno[i]);
			String name = annoName.length() == 0 ? ("arg" + i) : annoName;
			pd.append(name + ": ");
			pd.append(ClassUtil.classname(param[i]));
			pd.append(" ");
			pd.append(description(anno[i]));
		}
		return pd.toString();
	}
	public static String functionDoc(Method m) {
		return description(m) + "\n\n" + functionParamDoc(m);
	}
	public static String commandParamDoc(Method m) {
		return description(m) + "\n\n" + values(m);
	}




}
