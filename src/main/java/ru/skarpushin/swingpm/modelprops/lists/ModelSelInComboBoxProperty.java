package ru.skarpushin.swingpm.modelprops.lists;

import org.summerb.approaches.validation.ValidationError;

import com.google.common.base.Preconditions;

import ru.skarpushin.swingpm.collections.ListEx;
import ru.skarpushin.swingpm.modelprops.ModelProperty;
import ru.skarpushin.swingpm.valueadapters.ValueAdapter;

/**
 * Model which represents selection in combo property
 * 
 * @author sergeyk
 * 
 */
public class ModelSelInComboBoxProperty<E> extends ModelProperty<E> {
	protected final ModelListPropertyAccessor<E> optionsAccessor;
	protected final ModelListProperty<E> optionsProperty;
	private ModelSelInComboBoxPropertyAccessor<E> modelSelInComboBoxPropertyAccessor;

	public ModelSelInComboBoxProperty(Object source, ValueAdapter<E> valueAdapter, String propertyName,
			ModelListProperty<E> options) {
		this(source, valueAdapter, propertyName, options, null);
	}

	public ModelSelInComboBoxProperty(Object source, ValueAdapter<E> valueAdapter, String propertyName,
			ModelListProperty<E> options, ListEx<ValidationError> veSource) {
		super(source, valueAdapter, propertyName, veSource);
		Preconditions.checkArgument(options != null);

		this.optionsProperty = options;
		this.optionsAccessor = options.getModelListPropertyAccessor();

		// TODO: P3: Should we track if oprions list changed and currently
		// selected
		// item no more an option?
	}

	@Override
	public boolean setValueByOwner(E value) {
		if (!super.setValueByOwner(value)) {
			return false;
		}

		getModelSelInComboBoxPropertyAccessor().setSelectedItem(value);

		return true;
	};

	public ModelSelInComboBoxPropertyAccessor<E> getModelSelInComboBoxPropertyAccessor() {
		if (modelSelInComboBoxPropertyAccessor == null) {
			modelSelInComboBoxPropertyAccessor = new ComboBoxDefaultAccessorStrategy<E>(this);
		}
		return modelSelInComboBoxPropertyAccessor;
	}

	public void setModelSelInComboBoxPropertyAccessor(ModelSelInComboBoxPropertyAccessor<E> value) {
		modelSelInComboBoxPropertyAccessor = value;
	}

}
