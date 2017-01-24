#ifndef PDBQTIO_H
#define PDBQTIO_H

#include <stdio.h>
#include "pdbatom.h"

#ifdef __cplusplus
extern "C"
{
#endif

void load_pdbqt_file(pdb_atom_t *atoms, top_residue_atom_info_t *chis,
        const char *fname);


#ifdef __cplusplus
}
#endif

#endif


