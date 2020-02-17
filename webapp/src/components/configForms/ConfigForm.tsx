import React from "react"
import { useObserver } from "mobx-react"
import { Button, Snackbar, Paper } from "@material-ui/core"

import { Platform, JDKTestProject, JDKProject, Task } from "../../stores/model"
import JDKProjectForm from "./JDKProjectForm"
import TaskForm from "./TaskForm"
import PlatformForm from "./PlatformForm"
import JDKTestProjectForm from "./JDKTestProjectForm"
import useStores from "../../hooks/useStores"

interface SnackbarState {
    open: boolean
    message?: string
    actions?: JSX.Element[]
}

const ConfigForm: React.FC = () => {
    const { configStore } = useStores()

    return useObserver(() => {
        const { configState, editedConfig, selectedConfigGroupId } = configStore
        if (!selectedConfigGroupId) {
            return <div>{"ooops"}</div>
        }
        const {
            configError,
            discardOToolResponse,
            jobUpdateResults,
            submit
        } = configStore
        const okButton = (
            <Button
                color="secondary"
                key="ok"
                onClick={() => {
                    discardOToolResponse()
                }}
                size="small">
                OK
            </Button>
        )

        const snackbarState: SnackbarState | undefined =
            (configError && {
                open: true,
                message: configError,
                actions: [okButton]
            }) ||
            (jobUpdateResults && {
                open: true,
                message: "Done, see console output",
                actions: [okButton]
            })

        const renderForm = () => {
            switch (selectedConfigGroupId) {
                case "jdkProjects":
                    return (
                        <JDKProjectForm config={editedConfig as JDKProject} />
                    )
                case "jdkTestProjects":
                    return (
                        <JDKTestProjectForm
                            config={editedConfig as JDKTestProject}
                        />
                    )
                case "tasks":
                    return <TaskForm config={editedConfig as Task} />
                case "platforms":
                    return <PlatformForm config={editedConfig as Platform} />
                default:
                    return null
            }
        }

        return (
            <React.Fragment>
                <Paper style={{ padding: 20, width: "100%" }}>
                    {renderForm()}
                    <Button onClick={() => submit()} variant="contained">
                        {(configState === "edit" && "Edit") ||
                            (configState === "new" && "Create")}
                    </Button>
                </Paper>
                {snackbarState && (
                    <Snackbar
                        action={snackbarState.actions}
                        anchorOrigin={{
                            horizontal: "center",
                            vertical: "top"
                        }}
                        autoHideDuration={10000}
                        message={
                            <span>
                                {(snackbarState.message || "").toString()}
                            </span>
                        }
                        open={snackbarState.open}
                    />
                )}
            </React.Fragment>
        )
    })
}

export default ConfigForm
