package com.ashalmawia.coriolan;

@SuppressWarnings("unused")
public class TestCoriolanApplication extends CoriolanApplication {

	@Override
	protected void initializeDependencies() {
		// do nothing, as for now we don't need Koin for testing
	}

	@Override
	protected void runFirstStartRoutine() {
		// do nothing
	}
}
