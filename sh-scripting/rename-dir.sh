for ((i = 222; i <= 249; i++)); do
    oldname1=$(printf "TecelagemSemCostura5-maquina%01d" "$i")
    newname1=$(printf "TecelagemSemCostura5-maquina%02d" "$((i + 25))")
    oldname3=$(printf "AcabamentoSemCostura1-maquina%03d" "$i")
    newname3=$(printf "AcabamentoSemCostura1-maquina-%03d" "$((i + 1))")
    # if [[ -d $oldname1 && ! -d $newname1 ]]; then
        # mv "$oldname1" "$newname1"
        # echo mv -- "$oldname2" "$newname2"
    # fi

    if [[ -d $oldname3 && ! -d $newname3 ]]; then
        # echo mv -- "$oldname1" "$newname1"
        mv "$oldname3" "$newname3"
    fi

done
