import React from "react"
import Header from "./components/Header"
import ConfigForm from "./components/configForms/ConfigForm"
import ConfirmDialog from "./components/ConfirmDialog"
import ResultDialog from "./components/ResultDialog"
import { useObserver } from "mobx-react"

import { Grid } from "@material-ui/core"
import useStores from "./hooks/useStores"
import List from "./components/List"

const App: React.FC = () => {
    const { configStore, viewStore } = useStores()

    React.useEffect(() => {
        configStore.fetchJenkinsUrl()
        configStore.fetchConfigs()
    }, [configStore])

    const content = useObserver(() => {
        const { currentView } = viewStore
        switch (currentView) {
            case "form":
                return <ConfigForm />
            case "list":
                return <List />
        }
    })

    return (
        <React.Fragment>
            <Header />
            <Grid alignItems="center" container justify="center">
                <Grid item xs={11}>
                    {content}
                </Grid>
            </Grid>
            <ConfirmDialog />
            <ResultDialog />
        </React.Fragment>
    )
}

export default App
